import json
import os

output_dir = "integration"
os.makedirs(output_dir, exist_ok=True)

# --------------------------
# 配置
# --------------------------

crafters = ["farmer_crafting"]
category_config = {
    "materials_integration:dirt_integration": {
        "block": [
            "minecraft:coarse_dirt",
            "minecraft:grass_block",
            "minecraft:mud_brick_stairs",
            "minecraft:mud_brick_wall",
            "minecraft:mud_bricks",
            "minecraft:mud",
            "minecraft:mycelium",
            "minecraft:packed_mud",
            "minecraft:podzol",
            "minecraft:rooted_dirt",
        ],
        "slabs": ["minecraft:mud_brick_slab"],
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


def write_file(item, recipe):
    """写入JSON文件"""
    filename = item.split(":")[-1]
    path = os.path.join(output_dir, f"{filename}.json")
    with open(path, "w") as f:
        json.dump(recipe, f, indent=4)


if __name__ == "__main__":
    generate_recipe_tables()
    print("配方表生成完成！")
