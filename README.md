# MSChatRoom (MSCR)
An Android ~~chat application~~ application with login and registration function

[点我转到中文的README页面](https://github.com/MrShieh-X/mschatroom/blob/master/README-zh.md) <br/>
[Click me to go to the update log of this application](https://github.com/MrShieh-X/mschatroom/blob/master/update_logs.md) <br/>
## Copyright
MrShiehX own this application's copyright.<br/>
Anyone can take advices of this application to us.
## Version
The latest version: <br/>
<b>alpha-1 (Dec. 5, 2020)</b><br/>
Historical version: <br/>
<b>alpha-1 (Dec. 5, 2020) (First version)</b><br/>

## The software configuration required for this application is:
* Android 4.0 and above

## Supported languages
You can set the language in the settings.
- English (United States)
- Simplified Chinese (China)

## How to use
First, you need to have a server (if you don’t have one, you can use your local computer), and the MySQL database needs to be installed on this server. After you are ready, you need to create a new database in MySQL. "mscr" is the database name. You can change it.
```mysql
create database mscr;
```
Then we enter the database.
```mysql
use mscr;
```
After entering, create a new table in the following format</br>
![Table description](https://gitee.com/MrShiehX/Repository/raw/master/31.png "Table description")</br>
You can enter the following command to create a new table, where "users" is the name of the table.</br>
```mysql
create table `users` (`email` varchar(1000) not null,`account` varchar(1000) not null,`password` varchar(1000) not null,`information` mediumblob not null,`avatar` mediumblob);
```
At this point, if everything goes well without errors, then you can proceed to the next step.

After setting up the MySQL server, modify the variable content in the `Variables.java` file in the `src/java/com/mrshiehx/mschatroom` directory (representatives not listed in the table below need not be modified)

|Variables Name|Meaning|
|--------|:----:|
|SERVER_ADDRESS|Server (or local computer) address|
|DATABASE_NAME|MySQL database name|
|DATABASE_USER|MySQL database user|
|DATABASE_PASSWORD|MySQL database password|
|DATABASE_TABLE_NAME|MySQL table name|
|AUTHOR_MAIL|Author's email address (used to report errors to the author)|
|TEXT_ENCRYPTION_KEY|Text encryption key (not too long or too short)|

If you want to use another email address to send the CAPTCHA, you must first enable the IMAP/SMTP service and POP3/SMTP service of the email, and also modify the `SendEmailUtils.java` in the `src/java/com/mrshiehx/mschatroom/utils` directory variable content in the file

|Number of Lines of Code|Variable Name and Method Name|Meaning|
|-------------| -------------|:---------------:|
|20|HOST|The SMTP server address of the email address that sends the CAPTCHA email|
|24|from|Email address for sending CAPTCHA email|
|49|setSubject|CAPTCHA email title|
|53|setContent|CAPTCHA email content|
|61|username|The part in front of the email address symbol @ for sending the CAPTCHA email|
|64|password|The authorization code of the email address that sent the CAPTCHA email|

#### After all these tasks are completed, you can compile the application and run it.

## About Author
MrShiehX<br/>
- Occupation: <br/>
Student<br/>
- Email address: <br/>
Bntoylort@outlook.com<br/>
- QQ:<br/>
3553413882 (Remember to tell me why you want to add me)<br/>

## Since there is only one author who developed this application, there may be many bugs. If you find any bugs in this application or have new ideas, please send an email or add my QQ.
