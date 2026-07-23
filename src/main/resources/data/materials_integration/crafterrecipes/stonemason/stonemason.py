import json
import os

output_dir = "integration"
os.makedirs(output_dir, exist_ok=True)

# --------------------------
# 配置
# --------------------------

crafters = ["stonemason_crafting"]
category_config = {
    "materials_integration:sand_integration": {
        "block": ["minecraft:gravel", "minecraft:red_sand", "minecraft:sand"],
        "block_4": [
            "minecraft:chiseled_red_sandstone",
            "minecraft:chiseled_sandstone",
            "minecraft:cut_red_sandstone",
            "minecraft:cut_sandstone",
            "minecraft:red_sandstone_stairs",
            "minecraft:red_sandstone_wall",
            "minecraft:red_sandstone",
            "minecraft:sandstone_stairs",
            "minecraft:sandstone_wall",
            "minecraft:sandstone",
            "minecraft:smooth_red_sandstone_stairs",
            "minecraft:smooth_red_sandstone",
            "minecraft:smooth_sandstone_stairs",
            "minecraft:smooth_sandstone",
        ],
        "slabs": [
            "minecraft:cut_sandstone_slab",
            "minecraft:cut_red_sandstone_slab",
            "minecraft:red_sandstone_slab",
            "minecraft:sandstone_slab",
            "minecraft:smooth_red_sandstone_slab",
            "minecraft:smooth_sandstone_slab",
        ],
    },
    "materials_integration:stone_integration": {
        "block": [
            "minecraft:andesite_stairs",
            "minecraft:andesite_wall",
            "minecraft:andesite",
            "minecraft:calcite",
            "minecraft:chiseled_deepslate",
            "minecraft:cobbled_deepslate_stairs",
            "minecraft:cobbled_deepslate_wall",
            "minecraft:cobbled_deepslate",
            "minecraft:cobblestone_stairs",
            "minecraft:cobblestone_wall",
            "minecraft:cobblestone",
            "minecraft:deepslate_brick_stairs",
            "minecraft:deepslate_brick_wall",
            "minecraft:deepslate_bricks",
            "minecraft:deepslate_tile_stairs",
            "minecraft:deepslate_tile_wall",
            "minecraft:deepslate_tiles",
            "minecraft:deepslate",
            "minecraft:diorite_stairs",
            "minecraft:diorite_wall",
            "minecraft:diorite",
            "minecraft:dripstone_block",
            "minecraft:granite_stairs",
            "minecraft:granite_wall",
            "minecraft:granite",
            "minecraft:mossy_cobblestone_stairs",
            "minecraft:mossy_cobblestone_wall",
            "minecraft:mossy_cobblestone",
            "minecraft:polished_andesite_stairs",
            "minecraft:polished_andesite",
            "minecraft:polished_deepslate_stairs",
            "minecraft:polished_deepslate_wall",
            "minecraft:polished_deepslate",
            "minecraft:polished_diorite_stairs",
            "minecraft:polished_diorite",
            "minecraft:polished_granite_stairs",
            "minecraft:polished_granite",
            "minecraft:stone_stairs",
            "minecraft:stone",
            "minecraft:tuff",
        ],
        "slabs": [
            "minecraft:andesite_slab",
            "minecraft:cobbled_deepslate_slab",
            "minecraft:cobblestone_slab",
            "minecraft:deepslate_brick_slab",
            "minecraft:deepslate_tile_slab",
            "minecraft:diorite_slab",
            "minecraft:granite_slab",
            "minecraft:mossy_cobblestone_slab",
            "minecraft:polished_andesite_slab",
            "minecraft:polished_deepslate_slab",
            "minecraft:polished_diorite_slab",
            "minecraft:polished_granite_slab",
            "minecraft:stone_slab",
            "minecraft:stone_brick_slab",
        ],
    },
    "materials_integration:rock_integration": {
        "block": [
            "minecraft:chiseled_stone_bricks",
            "minecraft:cracked_stone_bricks",
            "minecraft:mossy_stone_brick_stairs",
            "minecraft:mossy_stone_brick_wall",
            "minecraft:mossy_stone_bricks",
            "minecraft:smooth_stone",
            "minecraft:stone_brick_stairs",
            "minecraft:stone_brick_wall",
            "minecraft:stone_bricks",
        ],
        "slabs": [
            "minecraft:mossy_stone_brick_slab",
            "minecraft:smooth_stone_slab",
            "minecraft:stone_brick_slab",
        ],
    },
}


# --------------------------
# 通用配置模板
# --------------------------
def create_block_recipe(crafter, item, category):
    """方块合成表"""
    return {
        "type": "recipe",
        "crafter": crafter,
        "intermediate": "minecraft:air",
        "inputs": [{"item": category, "count": 4}],
        "result": item,
        "count": 4,
    }


def create_block_4_recipe(crafter, item, category):
    """四倍合成表"""
    return {
        "type": "recipe",
        "crafter": crafter,
        "intermediate": "minecraft:air",
        "inputs": [{"item": category, "count": 16}],
        "result": item,
        "count": 4,
    }


def create_slabs_recipe(crafter, item, category):
    """台阶合成表"""
    return {
        "type": "recipe",
        "crafter": crafter,
        "intermediate": "minecraft:air",
        "inputs": [{"item": category, "count": 4}],
        "result": item,
        "count": 8,
    }


# --------------------------
# 生成逻辑
# --------------------------
def generate_recipe_tables():
    for crafter in crafters:
        for category, config_data in category_config.items():
            # 处理方块
            for item in config_data.get("block", []):
                recipe = create_block_recipe(crafter, item, category)
                write_file(item, recipe)
            # 处理四倍
            for item in config_data.get("block_4", []):
                recipe = create_block_4_recipe(crafter, item, category)
                write_file(item, recipe)
            # 处理台阶
            for item in config_data.get("slabs", []):
                recipe = create_slabs_recipe(crafter, item, category)
                write_file(item, recipe)


def write_file(item, recipe):
    """写入JSON文件"""
    filename = item.split(":")[-1]
    path = os.path.join(output_dir, f"{filename}.json")
    with open(path, "w") as f:
        json.dump(recipe, f, indent=4)


if __name__ == "__main__":
    generate_recipe_tables()
    print("配方表生成完成！")
