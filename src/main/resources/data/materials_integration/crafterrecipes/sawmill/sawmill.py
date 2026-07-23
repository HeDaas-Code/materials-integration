import json
import os

output_dir = "integration"
os.makedirs(output_dir, exist_ok=True)

# --------------------------
# 配置
# --------------------------

crafters = ["sawmill_crafting"]
category_config = {
    "materials_integration:log_integration": {
        "block": [
            "minecraft:acacia_log",
            "minecraft:acacia_wood",
            "minecraft:birch_log",
            "minecraft:birch_wood",
            "minecraft:cherry_log",
            "minecraft:cherry_wood",
            "minecraft:dark_oak_log",
            "minecraft:dark_oak_wood",
            "minecraft:jungle_log",
            "minecraft:jungle_wood",
            "minecraft:mangrove_log",
            "minecraft:mangrove_wood",
            "minecraft:oak_log",
            "minecraft:oak_wood",
            "minecraft:spruce_log",
            "minecraft:spruce_wood",
            "minecraft:stripped_acacia_log",
            "minecraft:stripped_acacia_wood",
            "minecraft:stripped_birch_log",
            "minecraft:stripped_birch_wood",
            "minecraft:stripped_cherry_log",
            "minecraft:stripped_cherry_wood",
            "minecraft:stripped_dark_oak_log",
            "minecraft:stripped_dark_oak_wood",
            "minecraft:stripped_jungle_log",
            "minecraft:stripped_jungle_wood",
            "minecraft:stripped_mangrove_log",
            "minecraft:stripped_mangrove_wood",
            "minecraft:stripped_oak_log",
            "minecraft:stripped_oak_wood",
            "minecraft:stripped_spruce_log",
            "minecraft:stripped_spruce_wood",
        ],
        "hanging_sign": [
            "minecraft:acacia_hanging_sign",
            "minecraft:birch_hanging_sign",
            "minecraft:cherry_hanging_sign",
            "minecraft:dark_oak_hanging_sign",
            "minecraft:jungle_hanging_sign",
            "minecraft:mangrove_hanging_sign",
            "minecraft:oak_hanging_sign",
            "minecraft:spruce_hanging_sign",
        ],
    },
    "materials_integration:planks_integration": {
        "block": [
            "minecraft:acacia_fence_gate",
            "minecraft:acacia_fence",
            "minecraft:acacia_planks",
            "minecraft:acacia_stairs",
            "minecraft:birch_fence_gate",
            "minecraft:birch_fence",
            "minecraft:birch_planks",
            "minecraft:birch_stairs",
            "minecraft:cherry_fence_gate",
            "minecraft:cherry_fence",
            "minecraft:cherry_planks",
            "minecraft:cherry_stairs",
            "minecraft:dark_oak_fence_gate",
            "minecraft:dark_oak_fence",
            "minecraft:dark_oak_planks",
            "minecraft:dark_oak_stairs",
            "minecraft:jungle_fence_gate",
            "minecraft:jungle_fence",
            "minecraft:jungle_planks",
            "minecraft:jungle_stairs",
            "minecraft:mangrove_fence_gate",
            "minecraft:mangrove_fence",
            "minecraft:mangrove_planks",
            "minecraft:mangrove_stairs",
            "minecraft:oak_fence_gate",
            "minecraft:oak_fence",
            "minecraft:oak_planks",
            "minecraft:oak_stairs",
            "minecraft:spruce_fence_gate",
            "minecraft:spruce_fence",
            "minecraft:spruce_planks",
            "minecraft:spruce_stairs",
            "minecraft:acacia_fence",
            "minecraft:birch_fence",
            "minecraft:cherry_fence",
            "minecraft:dark_oak_fence",
            "minecraft:jungle_fence",
            "minecraft:mangrove_fence",
            "minecraft:oak_fence",
            "minecraft:spruce_fence",
            "minecraft:acacia_fence_gate",
            "minecraft:birch_fence_gate",
            "minecraft:cherry_fence_gate",
            "minecraft:dark_oak_fence_gate",
            "minecraft:jungle_fence_gate",
            "minecraft:mangrove_fence_gate",
            "minecraft:oak_fence_gate",
            "minecraft:spruce_fence_gate",
            "minecraft:acacia_button",
            "minecraft:birch_button",
            "minecraft:cherry_button",
            "minecraft:dark_oak_button",
            "minecraft:jungle_button",
            "minecraft:mangrove_button",
            "minecraft:oak_button",
            "minecraft:spruce_button",
        ],
        "slabs": [
            "minecraft:acacia_slab",
            "minecraft:birch_slab",
            "minecraft:cherry_slab",
            "minecraft:dark_oak_slab",
            "minecraft:jungle_slab",
            "minecraft:mangrove_slab",
            "minecraft:oak_slab",
            "minecraft:spruce_slab",
        ],
        "door": [
            "minecraft:acacia_door",
            "minecraft:birch_door",
            "minecraft:cherry_door",
            "minecraft:dark_oak_door",
            "minecraft:jungle_door",
            "minecraft:mangrove_door",
            "minecraft:oak_door",
            "minecraft:spruce_door",
        ],
        "trapdoor": [
            "minecraft:acacia_trapdoor",
            "minecraft:birch_trapdoor",
            "minecraft:cherry_trapdoor",
            "minecraft:dark_oak_trapdoor",
            "minecraft:jungle_trapdoor",
            "minecraft:mangrove_trapdoor",
            "minecraft:oak_trapdoor",
            "minecraft:spruce_trapdoor",
        ],
        "sign": [
            "minecraft:acacia_sign",
            "minecraft:birch_sign",
            "minecraft:cherry_sign",
            "minecraft:dark_oak_sign",
            "minecraft:jungle_sign",
            "minecraft:mangrove_sign",
            "minecraft:oak_sign",
            "minecraft:spruce_sign",
        ],
        "pressure_plate": [
            "minecraft:acacia_pressure_plate",
            "minecraft:birch_pressure_plate",
            "minecraft:cherry_pressure_plate",
            "minecraft:dark_oak_pressure_plate",
            "minecraft:jungle_pressure_plate",
            "minecraft:mangrove_pressure_plate",
            "minecraft:oak_pressure_plate",
            "minecraft:spruce_pressure_plate",
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


def create_door_recipe(crafter, item, category):
    """门合成表"""
    return {
        "type": "recipe",
        "crafter": crafter,
        "intermediate": "minecraft:air",
        "inputs": [{"item": category, "count": 6}],
        "result": item,
        "count": 3,
    }


def create_trapdoor_recipe(crafter, item, category):
    """活板门合成表"""
    return {
        "type": "recipe",
        "crafter": crafter,
        "intermediate": "minecraft:air",
        "inputs": [{"item": category, "count": 6}],
        "result": item,
        "count": 2,
    }


def create_sign_recipe(crafter, item, category):
    """告示牌合成表"""
    return {
        "type": "recipe",
        "crafter": crafter,
        "intermediate": "minecraft:air",
        "inputs": [
            {"item": category, "count": 6},
            {"item": "minecraft:stick", "count": 1},
        ],
        "result": item,
        "count": 1,
    }


def create_hanging_sign_recipe(crafter, item, category):
    """悬挂告示牌合成表"""
    return {
        "type": "recipe",
        "crafter": crafter,
        "intermediate": "minecraft:air",
        "inputs": [
            {"item": category, "count": 6},
            {"item": "minecraft:chain", "count": 2},
        ],
        "result": item,
        "count": 6,
    }


def create_pressure_plate_recipe(crafter, item, category):
    """压力板合成表"""
    return {
        "type": "recipe",
        "crafter": crafter,
        "intermediate": "minecraft:air",
        "inputs": [{"item": category, "count": 2}],
        "result": item,
        "count": 1,
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
            # 处理台阶
            for item in config_data.get("slabs", []):
                recipe = create_slabs_recipe(crafter, item, category)
                write_file(item, recipe)
            # 处理门
            for item in config_data.get("door", []):
                recipe = create_door_recipe(crafter, item, category)
                write_file(item, recipe)
            # 处理活板门
            for item in config_data.get("trapdoor", []):
                recipe = create_trapdoor_recipe(crafter, item, category)
                write_file(item, recipe)
            # 处理告示牌
            for item in config_data.get("sign", []):
                recipe = create_sign_recipe(crafter, item, category)
                write_file(item, recipe)
            # 处理悬挂告示牌
            for item in config_data.get("hanging_sign", []):
                recipe = create_hanging_sign_recipe(crafter, item, category)
                write_file(item, recipe)
            # 处理压力板
            for item in config_data.get("pressure_plate", []):
                recipe = create_pressure_plate_recipe(crafter, item, category)
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
