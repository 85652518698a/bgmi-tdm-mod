package com.bgmitdm.map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WarehouseMap {
    private static final int MAP_SIZE = 80;
    private static final int MAP_HEIGHT = 10;
    private static final int WALL_HEIGHT = 6;
    private static final int CENTER_X = 0;
    private static final int CENTER_Z = 0;
    private static final int BASE_Y = 64;

    private final BlockState STONE_BRICKS = Blocks.STONE_BRICKS.defaultBlockState();
    private final BlockState SMOOTH_STONE = Blocks.SMOOTH_STONE.defaultBlockState();
    private final BlockState GLASS = Blocks.GLASS.defaultBlockState();
    private final BlockState RED_CONCRETE = Blocks.RED_CONCRETE.defaultBlockState();
    private final BlockState BLUE_CONCRETE = Blocks.BLUE_CONCRETE.defaultBlockState();
    private final BlockState BARREL = Blocks.BARREL.defaultBlockState();
    private final BlockState CHEST = Blocks.CHEST.defaultBlockState();
    private final BlockState AIR = Blocks.AIR.defaultBlockState();

    private ServerLevel level;
    private int centerX = CENTER_X;
    private int centerZ = CENTER_Z;
    private int baseY = BASE_Y;

    public void generate(ServerLevel level, BlockPos center) {
        this.level = level;
        this.centerX = center.getX();
        this.centerZ = center.getZ();
        this.baseY = center.getY();

        generateFloor();
        generateOuterWalls();
        generateInnerWalls();
        generateTeamSpawnAreas();
        generateCovers();
        generateRoof();
        clearInterior();
    }

    private void generateFloor() {
        int half = MAP_SIZE / 2;
        for (int x = -half; x <= half; x++) {
            for (int z = -half; z <= half; z++) {
                for (int y = 0; y < 3; y++) {
                    level.setBlock(new BlockPos(centerX + x, baseY - y, centerZ + z), SMOOTH_STONE, 3);
                }
            }
        }
    }

    private void generateOuterWalls() {
        int half = MAP_SIZE / 2;
        for (int x = -half; x <= half; x++) {
            for (int y = 0; y < WALL_HEIGHT; y++) {
                level.setBlock(new BlockPos(centerX + x, baseY + y, centerZ - half), STONE_BRICKS, 3);
                level.setBlock(new BlockPos(centerX + x, baseY + y, centerZ + half), STONE_BRICKS, 3);
            }
        }
        for (int z = -half; z <= half; z++) {
            for (int y = 0; y < WALL_HEIGHT; y++) {
                level.setBlock(new BlockPos(centerX - half, baseY + y, centerZ + z), STONE_BRICKS, 3);
                level.setBlock(new BlockPos(centerX + half, baseY + y, centerZ + z), STONE_BRICKS, 3);
            }
        }
    }

    private void generateInnerWalls() {
        int half = MAP_SIZE / 4;
        for (int z = -half; z <= half; z += 4) {
            for (int y = 0; y < 3; y++) {
                level.setBlock(new BlockPos(centerX, baseY + y, centerZ + z), RED_CONCRETE, 3);
                level.setBlock(new BlockPos(centerX + 2, baseY + y, centerZ + z + 2), BLUE_CONCRETE, 3);
                level.setBlock(new BlockPos(centerX - 2, baseY + y, centerZ + z - 2), RED_CONCRETE, 3);
            }
        }

        for (int x = -half; x <= half; x += 4) {
            for (int y = 0; y < 3; y++) {
                level.setBlock(new BlockPos(centerX + x, baseY + y, centerZ), BLUE_CONCRETE, 3);
                level.setBlock(new BlockPos(centerX + x + 2, baseY + y, centerZ + 2), RED_CONCRETE, 3);
                level.setBlock(new BlockPos(centerX + x - 2, baseY + y, centerZ - 2), BLUE_CONCRETE, 3);
            }
        }
    }

    private void generateTeamSpawnAreas() {
        int half = MAP_SIZE / 2;
        int spawnDepth = 5;

        for (int x = -spawnDepth; x <= spawnDepth; x++) {
            for (int z = half - 3; z <= half; z++) {
                for (int y = 0; y < 2; y++) {
                    level.setBlock(new BlockPos(centerX + x, baseY + y, centerZ + z), RED_CONCRETE, 3);
                }
            }
        }

        for (int x = -spawnDepth; x <= spawnDepth; x++) {
            for (int z = -half; z <= -half + 3; z++) {
                for (int y = 0; y < 2; y++) {
                    level.setBlock(new BlockPos(centerX + x, baseY + y, centerZ + z), BLUE_CONCRETE, 3);
                }
            }
        }
    }

    private void generateCovers() {
        int half = MAP_SIZE / 2;
        int[][] cratePositions = {
                {-15, -15}, {-10, -10}, {-5, -5}, {5, 5}, {10, 10}, {15, 15},
                {-15, 15}, {-10, 10}, {-5, 5}, {5, -5}, {10, -10}, {15, -15},
                {-20, 0}, {20, 0}, {0, -20}, {0, 20},
                {-8, -20}, {8, 20}, {-20, -8}, {20, 8},
                {-12, 12}, {12, -12}, {-18, -5}, {18, 5}
        };

        for (int[] pos : cratePositions) {
            BlockPos crateBase = new BlockPos(centerX + pos[0], baseY, centerZ + pos[1]);
            for (int y = 0; y < 2; y++) {
                level.setBlock(crateBase.above(y), y % 2 == 0 ? BARREL : CHEST, 3);
            }
        }
    }

    private void generateRoof() {
        int half = MAP_SIZE / 2;
        for (int x = -half; x <= half; x++) {
            for (int z = -half; z <= half; z++) {
                if ((x + z) % 3 == 0) {
                    level.setBlock(new BlockPos(centerX + x, baseY + WALL_HEIGHT, centerZ + z), GLASS, 3);
                }
            }
        }

        for (int x = -half; x <= half; x++) {
            level.setBlock(new BlockPos(centerX + x, baseY + WALL_HEIGHT, centerZ - half), STONE_BRICKS, 3);
            level.setBlock(new BlockPos(centerX + x, baseY + WALL_HEIGHT, centerZ + half), STONE_BRICKS, 3);
        }
        for (int z = -half; z <= half; z++) {
            level.setBlock(new BlockPos(centerX - half, baseY + WALL_HEIGHT, centerZ + z), STONE_BRICKS, 3);
            level.setBlock(new BlockPos(centerX + half, baseY + WALL_HEIGHT, centerZ + z), STONE_BRICKS, 3);
        }
    }

    private void clearInterior() {
        int half = MAP_SIZE / 2 - 1;
        for (int x = -half; x <= half; x++) {
            for (int z = -half; z <= half; z++) {
                for (int y = 1; y < WALL_HEIGHT; y++) {
                    BlockPos pos = new BlockPos(centerX + x, baseY + y, centerZ + z);
                    if (level.getBlockState(pos).is(Blocks.STONE_BRICKS) ||
                            level.getBlockState(pos).is(Blocks.RED_CONCRETE) ||
                            level.getBlockState(pos).is(Blocks.BLUE_CONCRETE) ||
                            level.getBlockState(pos).is(Blocks.BARREL) ||
                            level.getBlockState(pos).is(Blocks.CHEST)) {
                        continue;
                    }
                    if (!level.getBlockState(pos).is(Blocks.GLASS)) {
                        level.setBlock(pos, AIR, 3);
                    }
                }
            }
        }
    }

    public BlockPos getRedSpawn() {
        return new BlockPos(centerX, baseY, centerZ + MAP_SIZE / 2 - 2);
    }

    public BlockPos getBlueSpawn() {
        return new BlockPos(centerX, baseY, centerZ - MAP_SIZE / 2 + 2);
    }
}
