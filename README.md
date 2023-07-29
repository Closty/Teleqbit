# 目的
本程序连接telegrambot和qbittorrent，通过telegram聊天窗口控制查看qbittorrent情况，目前只能运用qbittorrent的主要功能。如需立即体验可前往本人正在维护的[emby公益点播服](https://t.me/tembygroup)，其中的点播功能就是本程序的一部分。

```
发送包含http或magnet的链接时，将自动添加该种子
status - 查询磁盘剩余及下载情况
status_downloading - 查询正在下载任务进度
pause_all - 暂停所有下载任务
resume_all - 继续所有下载任务
delete_all - 删除所有下载任务
about - 关于
```

# Docker一键部署

```
docker run \
--name teleqbit \
-e QB_IP='http://替换成qbittorrent的服务器地址:端口/api/v2/' \
-e QB_NAME='替换成qbittorrent的账号' \
-e QB_PASSWORD='替换成qbittorrent的密码' \
-e BOT_TOKEN='替换成telegram bot的token' \
-e BOT_NAME='替换成telegram bot的name不包含@' \
closty/teleqbit
```

### 实例
```
docker run \
--name teleqbit \
-e QB_IP='http://101.73.233.123:8080/api/v2/' \
-e QB_NAME='admin' \
-e QB_PASSWORD='adminadmin' \
-e BOT_TOKEN='1290117084:AAH2pwEoe7pQ_SFVcq8UR2hENyf31Pqeo' \
-e BOT_NAME='TeleqBot' \
closty/teleqbit
```

# 相关截图
<img src="https://s2.loli.net/2023/07/30/iBxXqHZUptF2lM8.png"   width=30%>
<img src="https://s2.loli.net/2023/07/30/SBtbmZX4rh1jsTx.png"   width=30%>
<img src="https://s2.loli.net/2023/07/30/6xu38R7CytMm4qL.jpg"  width=30%>

# TODO
- 拆包
- 增加翻页按钮

