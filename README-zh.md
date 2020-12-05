# MS聊天室（MSCR）
一个 Android 的~~聊天软件~~拥有登录注册功能的应用程序

[Click me to go to the README in English](https://github.com/MrShieh-X/mschatroom/blob/master/README.md) <br/>
[点我转到本应用程序的更新日志](https://github.com/MrShieh-X/mschatroom/blob/master/update_logs-zh.md) <br/>
## 版权
MrShiehX 拥有该应用程序的版权。<br/>
任何人都可以对此应用程序提出意见和建议。
## 版本
最新版本：<br/>
<b>alpha-1 (2020年12月05日)</b><br/>
历史版本：<br/>
<b>alpha-1 (2020年12月05日)（第一个版本）</b><br/>

## 本应用程序需要的软件配置有：
* Android 4.0及以上

## 支持的语言
您可在设置中设置语言。
- 英语（美国）
- 简体中文（中国）

## 如何使用
首先，您需要拥有一个服务器（如果您没有，可以使用本地计算机），并且此服务器上需要安装MySQL数据库，这些您都准备好后，需要在MySQL里新建一个数据库，“mscr”是数据库名称，可更改。
```mysql
create database mscr;
```
然后我们进入该数据库。
```mysql
use mscr;
```
进入后，新建一个格式如下图的表格
![表格描述](https://gitee.com/MrShiehX/Repository/raw/master/31.png "表格描述")
您可以输入以下命令来新建表格，其中“users”是表格名。
```mysql
create table `users` (`email` varchar(1000) not null,`account` varchar(1000) not null,`password` varchar(1000) not null,`information` mediumblob not null,`avatar` mediumblob);
```
至此，如果一切顺利无错误，那么，您就可以进行下一步了。

搭建完拥有MySQL的服务器后，修改 `src/java/com/mrshiehx/mschatroom` 目录里的 `Variables.java` 文件里的变量内容（下表没有列出来的代表可不用修改）

| 变量名|含义|
| --------|:----:|
| SERVER_ADDRESS|服务器（或本地计算机）地址|
| DATABASE_NAME|MySQL数据库名称|
| DATABASE_USER|MySQL数据库账号|
| DATABASE_PASSWORD|MySQL数据库密码|
| DATABASE_TABLE_NAME|MySQL表格名称|
| AUTHOR_MAIL|作者电子邮箱地址（用于出现错误时反馈给作者）|
| TEXT_ENCRYPTION_KEY|文本加密密钥（不能太长，也不能太短）|

如果要使用其他邮箱来发送验证码，首先要开启邮箱的IMAP/SMTP服务和POP3/SMTP服务，还要修改 `src/java/com/mrshiehx/mschatroom/utils` 目录里的 `SendEmailUtils.java` 文件里的变量内容

|所在的行数|变量名和方法名|含义|
|-------------| -------------|:---------------:|
|20|HOST|发送验证码邮件的邮箱的SMTP服务器地址  |
|24|from|发送验证码邮件的邮箱地址|
|49|setSubject|验证码邮件标题|
|53|setContent|验证码邮件内容|
|61|username|发送验证码邮件的邮箱地址符号@前面的部分|
|64|password|发送验证码邮件的邮箱的授权码|
这些工作都做完后，您就可以编译该应用程序并运行了。

## 关于作者
MrShiehX<br/>
- 职业：<br/>
学生<br/>
- 邮箱：<br/>
Bntoylort@outlook.com<br/>
- QQ：<br/>
3553413882（备注来意）<br/>

## 因为开发此应用程序的作者只有一人，所以BUG可能会多，如果你在本Mod发现任何BUG，或者有新的想法，欢迎发送邮件或添加我的QQ。
