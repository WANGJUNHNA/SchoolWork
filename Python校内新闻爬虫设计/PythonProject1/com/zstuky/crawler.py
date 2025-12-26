import requests
from bs4 import BeautifulSoup
import pymongo
from datetime import datetime
import time

#基础配置（数据库存储使用MongoDB）
# 官网域名（基础URL）
BASE_URL = "https://www.ky.zstu.edu.cn"
# 新闻列表页URL（学院新闻板块）
NEWS_LIST_URL = "https://www.ky.zstu.edu.cn/kyyw.htm"
# MongoDB配置
MONGO_HOST = "localhost"  # 本地MongoDB
MONGO_PORT = 27017  # 默认端口
MONGO_DB = "zstu_news"  # 数据库名
MONGO_COLLECTION = "school_news"  # 集合名
# 请求头
HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 SLBrowser/9.0.6.8151 SLBChan/112 SLBVPV/64-bit"
}


# MongoDB连接
def connect_mongo():
    try:
        # 建立连接
        client = pymongo.MongoClient(MONGO_HOST, MONGO_PORT)
        # 创建数据库
        db = client[MONGO_DB]
        # 创建集合
        collection = db[MONGO_COLLECTION]
        print("MongoDB连接成功！")
        return collection
    except Exception as e:
        print(f"MongoDB连接失败：{e}")
        return None


#解析新闻列表页
def parse_news_list(list_url):
    news_list = []
    try:
        response = requests.get(list_url, headers=HEADERS, timeout=10)
        response.encoding = response.apparent_encoding
        soup = BeautifulSoup(response.text, "lxml")

        # 1. 找到新闻列表的根容器
        wlistee_div = soup.find("div", class_="wlistee")
        if not wlistee_div:
            print("未找到新闻列表的根容器（div.wlistee）")
            return news_list

        news_ul = wlistee_div.find("ul")  # 找到ul标签（新闻列表的父容器）
        if not news_ul:
            print("div.wlistee内未找到ul标签")
            return news_list

        # 2. 匹配所有新闻节点：ul下的所有li标签
        news_items = news_ul.find_all("li")
        print(f"当前页匹配到 {len(news_items)} 条新闻节点")

        # 3. 遍历每个li，提取数据
        for item in news_items:
            # 提取a标签
            a_tag = item.find("a")
            if not a_tag:
                continue

            # 提取标题
            title = a_tag.get_text(strip=True)

            # 提取详情页URL
            detail_url = a_tag.get("href")
            if detail_url.startswith("#"):
                detail_url = detail_url[1:]
            # 拼接完整URL
            if not detail_url.startswith(("http://", "https://")):
                detail_url = BASE_URL + "/" + detail_url.lstrip("/")

            # 提取发布时间（li内的span标签文本）
            time_span = item.find("span")
            publish_time = time_span.get_text(strip=True) if time_span else "未知时间"

            # 存储单条新闻信息
            news_list.append({
                "title": title,
                "detail_url": detail_url,
                "publish_time": publish_time
            })
            print(f"已获取新闻：{title}")

    except Exception as e:
        print(f"列表页爬取报错：{str(e)}")
    return news_list


#解析新闻详情页
def parse_news_detail(detail_url):
    content = ""
    try:
        # 发送请求
        time.sleep(1)  # 间隔1秒
        response = requests.get(detail_url, headers=HEADERS, timeout=10)
        response.encoding = response.apparent_encoding
        soup = BeautifulSoup(response.text, "lxml")
        # 提取正文
        content_tag = soup.find("div", class_="article-content")
        if content_tag:
            content = content_tag.get_text(strip=True, separator="\n")
        else:
            content_tag = soup.find("div", class_="content")
            if content_tag:
                content = content_tag.get_text(strip=True, separator="\n")
            else:
                content = "未获取到正文"
    except Exception as e:
        print(f"详情页爬取失败（{detail_url}）：{e}")
    return content


#爬取和存储
def main():
    # 1. 连接MongoDB
    collection = connect_mongo()
    if not collection:
        return

    # 2. 爬取新闻列表
    news_list = parse_news_list(NEWS_LIST_URL)
    if not news_list:
        print("未获取到新闻数据，爬取终止")
        return

    # 3. 遍历新闻列表，爬取详情并存储到MongoDB
    success_count = 0
    for news in news_list:
        # 爬取正文
        content = parse_news_detail(news["detail_url"])
        full_news = {
            "title": news["title"],
            "detail_url": news["detail_url"],
            "publish_time": news["publish_time"],
            "content": content,
            "crawl_time": datetime.now().strftime("%Y-%m-%d %H:%M:%S")  # 爬取时间（格式化）
        }

        # 存储到MongoDB
        if collection.find_one({"detail_url": news["detail_url"]}):
            print(f"新闻已存在：{news['title']}，跳过存储")
            continue

        # 插入数据
        collection.insert_one(full_news)
        success_count += 1
        print(f"存储成功：{news['title']}")

    # 4. 爬取完成统计
    print("=" * 50)
    print(f"爬取任务完成！")
    print(f"总获取新闻数：{len(news_list)}")
    print(f"成功存储到MongoDB数：{success_count}")
    print("=" * 50)

#启动爬虫程序
if __name__ == "__main__":
    main()