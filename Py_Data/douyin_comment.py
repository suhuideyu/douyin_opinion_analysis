from DrissionPage import ChromiumPage
from datetime import datetime
import csv
import os
import sys
import time

# 解决 Windows 控制台 GBK 编码问题
if sys.stdout.encoding and sys.stdout.encoding.upper() != 'UTF-8':
    import io
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')

# ====================== 配置 ======================
SAVE_DIR = r"D:\idea_workspace\DY-Comment\Py_Data\Datas"
LISTEN_API = "aweme/v1/web/comment/list"
# ===================================================

# 视频编号：优先从命令行参数读取，否则使用默认值
if len(sys.argv) > 1:
    VIDEO_ID = sys.argv[1]
else:
    VIDEO_ID = "7613237683142223214"

# 采集数量：从命令行参数读取，否则 300
if len(sys.argv) > 2:
    MAX_COMMENTS = int(sys.argv[2])
else:
    MAX_COMMENTS = 300

# 如果输入已是完整链接（含 http 或 douyin.com），直接使用；否则拼接为视频页地址
if VIDEO_ID.startswith("http") or "douyin.com" in VIDEO_ID:
    TARGET_URL = VIDEO_ID
else:
    TARGET_URL = f"https://www.douyin.com/video/{VIDEO_ID}"

os.makedirs(SAVE_DIR, exist_ok=True)
# ======================================================

CONTAINER_JS = 'document.querySelector(".parent-route-container") || document.querySelector("#douyin-right-container>div:nth-child(2)")'


def scroll_comment_panel(dp):
    """翻页：向下滚动内部滚动容器，触发加载更多评论"""
    dp.run_js(f'var c={CONTAINER_JS}; if(c) c.scrollBy(0, 800);')
    time.sleep(2)


data_list = []
csv_path = os.path.join(SAVE_DIR, f"{VIDEO_ID}.csv")

try:
    dp = ChromiumPage()
    print("( ‵▽′)ψ 浏览器启动成功")

    dp.listen.start(LISTEN_API)
    dp.get(TARGET_URL)
    print("( ‵▽′)ψ 抖音页面已打开，请等待评论加载...")
    time.sleep(5)

    # 从最终地址栏提取真实视频 ID
    import re
    m = re.search(r'/video/(\d+)', dp.url)
    if m:
        VIDEO_ID = m.group(1)
    csv_path = os.path.join(SAVE_DIR, f"{VIDEO_ID}.csv")
    print(f"  视频ID: {VIDEO_ID}")

    # 滚动内部容器以触发评论区加载
    dp.run_js(f'var c={CONTAINER_JS}; if(c) c.scrollTop = 600;')
    time.sleep(3)
    print("(●ˇ∀ˇ●) 已滚动到评论区")

    page = 0
    while len(data_list) < MAX_COMMENTS:
        page += 1
        print(f"\n ( •̀ ω •́ )y 正在采集第 {page} 页")

        resp = dp.listen.wait(timeout=15)
        if not resp:
            print("w(ﾟДﾟ)w 未捕获到评论数据包，结束采集")
            break

        json_data = resp.response.body
        if "comments" not in json_data or not json_data["comments"]:
            print("w(ﾟДﾟ)w 无更多评论数据，结束采集")
            break

        page_count = len(json_data["comments"])
        for c in json_data["comments"]:
            if len(data_list) >= MAX_COMMENTS:
                break
            try:
                dt = datetime.fromtimestamp(c["create_time"]).strftime("%Y-%m-%d %H:%M:%S")
                data_list.append({
                    "昵称": c["user"]["nickname"],
                    "地区": c.get("ip_label", "未知"),
                    "日期": dt,
                    "内容": c["text"],
                    "点赞": c["digg_count"]
                })
                print(f"  {c['user']['nickname']}｜{c.get('ip_label', '未知')}｜{dt}｜点赞{c['digg_count']}｜{c['text'][:30]}...")
            except Exception as e:
                print(f"w(ﾟДﾟ)w 解析单条评论失败：{e}")
                continue

        print(f"（￣︶￣）↗ 本页 {page_count} 条，累计 {len(data_list)} 条")

        if len(data_list) >= MAX_COMMENTS:
            print(f"( •̀ ω •́ )✧ 已达到 {MAX_COMMENTS} 条上限，结束采集")
            break

        has_more = json_data.get("has_more", 0)
        if not has_more:
            print("(○´･д･)ﾉ 已是最后一页，结束采集")
            break
        scroll_comment_panel(dp)

    if data_list:
        with open(csv_path, "w", encoding="utf-8-sig", newline="") as f:
            writer = csv.DictWriter(f, fieldnames=["昵称", "地区", "日期", "内容", "点赞"])
            writer.writeheader()
            writer.writerows(data_list)
        print(f"\n φ(゜▽゜*)♪ 采集成功！共 {len(data_list)} 条评论，已保存到：{csv_path}")
    else:
        print("\n =.= 未采集到任何数据，不生成文件")

except Exception as e:
    print(f"\n =.= 采集失败：{e}")
    print("本次不生成新CSV文件，下次运行仍沿用当前序号")
