# MSChatRoom (MSCR)
An Android ~~chat application~~ application with a login and registration function using MySQL database

[点我转到中文的README页面](https://github.com/MrShieh-X/mschatroom/blob/master/README-zh.md) <br/>
[Click me to go to the update log of this application](https://github.com/MrShieh-X/mschatroom/blob/master/update_logs.md) <br/>
## Copyright
MrShiehX own this application's copyright.<br/>
Anyone can take advices of this application to us.

## Version
The latest version: <br/>
<b>alpha-5 (Dec. 26, 2020)</b><br/>
Historical version: <br/>
<b>alpha-5 (Dec. 26, 2020)</b><br/>
<b>alpha-4 (Dec. 20, 2020)</b><br/>
<b>alpha-3 (Dec. 17, 2020)</b><br/>
<b>alpha-2 (Dec. 7, 2020)</b><br/>
<b>alpha-1 (Dec. 5, 2020) (First version)</b><br/>

## The software configuration required for this application is:
* Android 4.0 and above

## Supported languages
You can set the language in the settings.
- English (United States)
- Simplified Chinese (China)

## Found BUGs
- When adding a chat, if the current number of chats >= 2, it will replace the last one, but it has been added successfully. Restart the application to see the newly added chat.
- The application may crash after modifying the user information for unknown reasons.
- (Fixed in alpha-3) After you login, open the settings, the user information (avatar, name and what's up) may not be displayed. This problem will be fixed in the future.

## Precautions
- If you want to add a chat, make sure that the target user and you are using the same encryption or decryption algorithm.

## Text encryption and decryption
Use the encryption or decryption algorithm of the `src/java/com/mrshiehx/mschatroom/utils/EnDeCryptTextUtils.java` class

## Working Principle
### Register
After encrypting the email address, account number and password, use the `insert` command to create new data in the MySQL database.
### Login
After encrypting the email address or account number and password, use the `select` command to check whether the data exists in the MySQL database. If it exists, store the login data in SharedPreference.
### Reset password
After encrypting the email address and password, check whether the account exists. If it exists, use the `update` command to modify the password data.
### Login with email address
After encrypting the email address, use the `select` command in the MySQL database to check whether the data exists. If it does, the password is obtained and the login data is stored in SharedPreference.
### Log out
Delete the email address or account and password data in SharedPreference.
### Delete account
After encrypting the email address or account number and password, use the `delete` command to delete the data in the MySQL database and log out.

## Ready to work
First of all, you need to have a server (if you don’t have one, you can use your local computer), and the MySQL database needs to be installed on this server. After you are ready, you need to create a new database in MySQL. "mscr" is the database name, you can change it.
```mysql
create database mscr;
```
Then we enter the database.
```mysql
use mscr;
```
After entering, create a new table in the following format</br>
![Table description](https://gitee.com/MrShiehX/Repository/raw/master/31.png "Table description")</br>
You can enter the following command to create a new table, where "users" is the name of the table. </br>
```mysql
create table `users` (`email` varchar(1000) not null,`account` varchar(1000) not null,`password` varchar(1000) not null,`information` mediumblob not null,`avatar` mediumblob);
```
At this point, if everything goes well without errors, then you can proceed to the next step.

After setting up a server with MySQL, modify the variable content in the file `Variables.java` in the `src/java/com/mrshiehx/mschatroom` directory (representatives not listed in the table below do not need to be modified)

| Variable name|Meaning|
| --------|:----:|
| SERVER_ADDRESS|Server (or local computer) address|
| DATABASE_NAME|MySQL database name|
| DATABASE_USER|MySQL database account|
| DATABASE_PASSWORD|MySQL database password|
| DATABASE_TABLE_NAME|MySQL table name|
| AUTHOR_MAIL|Author's email address (sed to give feedback to the author when there is an error and contact the author about the interface)|
| TEXT_ENCRYPTION_KEY|Text encryption key (not too long or too short)|
| CAPTCHA_EMAIL_SMTP_SERVER_ADDRESS|The SMTP server address of the mailbox that sends the CAPTCHA email (see below)|
| CAPTCHA_EMAIL_ADDRESS|The email address for sending the CAPTCHA email (see below)|
| AUTHENTICATOR|Authorization code of the mailbox sending the verification code email (see below)|

Regarding the e-mail address for sending the CAPTCHA, it is not provided here. You need to create an email address yourself, and then enable the IMAP/SMTP service and POP3/SMTP service of the mailbox, and write down the email address, authorization code and SMTP address of the email service , Input in `Variables.java`. You can also choose to modify the method content in the `SendEmailUtils.java` file in the `src/java/com/mrshiehx/mschatroom/utils` directory

|The number of rows|Method name|Meaning|
|-------------| -------------|:---------------:|
|47|setSubject|CAPTCHA email title|
|51|setContent|CAPTCHA email content|

#### After all these tasks are completed, you can compile the application and run it.

## About Author
MrShiehX<br/>
- Occupation: <br/>
Student<br/>
- Email address: <br/>
Bntoylort@outlook.com<br/>
- QQ:<br/>
3553413882 (Remember to tell me why you want to add me)<br/>

## If you find any bugs in this application or have new ideas, please send an email or add my QQ.
