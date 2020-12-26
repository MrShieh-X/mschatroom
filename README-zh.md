# MS聊天室（MSCR）
一个使用 MySQL 数据库的 Android 的~~聊天软件~~拥有登录注册功能的应用程序

[Click me to go to the README in English](https://github.com/MrShieh-X/mschatroom/blob/master/README.md) <br/>
[点我转到本应用程序的更新日志](https://github.com/MrShieh-X/mschatroom/blob/master/update_logs-zh.md) <br/>
## 版权
MrShiehX 拥有该应用程序的版权。<br/>
任何人都可以对此应用程序提出意见和建议。
## 版本
最新版本：<br/>
<b>alpha-5 (2020年12月26日)</b><br/>
历史版本：<br/>
<b>alpha-5 (2020年12月26日)</b><br/>
<b>alpha-4 (2020年12月20日)</b><br/>
<b>alpha-3 (2020年12月17日)</b><br/>
<b>alpha-2 (2020年12月07日)</b><br/>
<b>alpha-1 (2020年12月05日)（第一个版本）</b><br/>

## 本应用程序需要的软件配置有：
* Android 4.0及以上

## 支持的语言
您可在设置中设置语言。
- 英语（美国）
- 简体中文（中国）

## 已发现的漏洞
- 添加聊天时，当前聊天数量>=2，就会替换最后一个，但是它已经添加成功，重启应用即可看到新添加的聊天。
- 修改用户信息有几率会闪退，原因未知。
- （已在alpha-3中修复）在您登录后，打开设置，可能会显示不出用户信息（头像、昵称和个性签名），此问题将会在以后修复。

## 注意事项
- 如果您要添加聊天，请确保目标用户和您使用的是同一个加密或解密算法。

## 文本加密和解密
使用`src/java/com/mrshiehx/mschatroom/utils/EnDeCryptTextUtils.java`类的加密或解密算法

## 工作原理
### 注册
把邮箱地址、账号和密码加密后，在 MySQL 数据库使用`insert`命令里新建数据。
### 登录
把邮箱地址或账号和密码加密后，在 MySQL 数据库使用`select`命令检测数据是否存在，如果存在，则在 SharedPreference 里存储登录数据。
### 找回密码
把邮箱地址和密码加密后，检测账户是否存在，如果存在，使用`update`命令修改密码数据。
### 使用邮箱地址登录
把邮箱地址加密后，在 MySQL 数据库使用`select`命令检测数据是否存在，如果存在，则获得密码后，在 SharedPreference 里存储登录数据。
### 退出登录
在 SharedPreference 里删除邮箱地址或账号和密码数据。
### 删除账户
把邮箱地址或账号和密码加密后，在 MySQL 数据库使用`delete`命令删除数据，并退出登录。

## 准备工作
首先，您需要拥有一个服务器（如果您没有，可以使用本地计算机），并且此服务器上需要安装MySQL数据库，这些您都准备好后，需要在MySQL里新建一个数据库，“mscr”是数据库名称，可更改。
```mysql
create database mscr;
```
然后我们进入该数据库。
```mysql
use mscr;
```
进入后，新建一个格式如下图的表格</br>
![表格描述](https://gitee.com/MrShiehX/Repository/raw/master/31.png "表格描述")</br>
您可以输入以下命令来新建表格，其中“users”是表格名。</br>
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
| AUTHOR_MAIL|作者电子邮箱地址（用于出现错误时反馈给作者和关于界面联系作者）|
| TEXT_ENCRYPTION_KEY|文本加密密钥（不能太长，也不能太短）|
| CAPTCHA_EMAIL_SMTP_SERVER_ADDRESS|发送验证码邮件的邮箱的SMTP服务器地址（见下文）|
| CAPTCHA_EMAIL_ADDRESS|发送验证码邮件的邮箱地址（见下文）|
| AUTHENTICATOR|发送验证码邮件的邮箱的授权码（见下文）|

关于发送验证码的电子邮箱，这里暂时不提供，需要您自行创建一个电子邮箱，之后要开启邮箱的IMAP/SMTP服务和POP3/SMTP服务，记下邮箱地址、授权码和该邮箱服务的SMTP地址，输入在 `Variables.java` 中。您还可以选择修改 `src/java/com/mrshiehx/mschatroom/utils` 目录里的 `SendEmailUtils.java` 文件里的方法内容

|所在的行数|方法名|含义|
|-------------| -------------|:---------------:|
|47|setSubject|验证码邮件标题|
|51|setContent|验证码邮件内容|

#### 这些工作都做完后，您就可以编译该应用程序并运行了。

## 关于作者
MrShiehX<br/>
- 职业：<br/>
学生<br/>
- 邮箱：<br/>
Bntoylort@outlook.com<br/>
- QQ：<br/>
3553413882（备注来意）<br/>

## 如果您在本应用程序发现任何BUG，或者有新的想法，欢迎发送邮件或添加我的QQ。

