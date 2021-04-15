# Update Logs
Currently, the latest version is 1.2.1, which was updated on April 15, 2021.

## 1.2.1 (Apr. 15, 2021)
- Support to reset password.
- Change "Modify User Information" to "Account Profile".

## 1.2 (Apr. 14, 2021)
- Support sending pictures.
- Modified the start page.
- Optimize and fix some problems.

## 1.1.1 (Apr. 7, 2021)
- Fixed some issues.

## 1.1 (Apr. 5, 2021)
- The dialog for setting server information has been deleted.
- Removed Developer Options.
- Solved the problem that the chat screen would not automatically slide down after receiving a message.
- Fixed some issues.

## 1.0 (Jan. 31, 2021)
- Added the chat function, chat notification function, offline messages storage function.
- In the main interface, the latest message of each chat and the sending time of the latest message will be displayed.
- Added an option to visit the Gitee repository of this application on the About interface.
- Fixed some problems.

## beta-2 (Jan. 26, 2021)
- Added the chat interface.
- Added dialog to decide whether to delete chat
- You can delete the chat files when deleting account.
- Fixed some problems.

## beta-1 (Jan. 21, 2021)
- (Some models may be invalid) Added the function of clearing cache and clearing application data, you can see them on the Settings interface.
- (Some models may be invalid) Logout can choose whether to delete the added chats and chat files.
- The first startup will get whether the system display interface is a dark theme or a light theme, and save it in SharedPreferences.
- Fixed the problem that the email address cannot be used to login.
- Fixed the problem that may crash after modifying user information.
- Fixed the problem that language and theme information could not be saved when starting the app for the first time.
- Optimized the code to Modify User Information interface to make this interface more stable.
- Added the "Verifying" dialog to verify the password after entering the password of the deleted account.
- Added the function of deleting chats. This option is located in the menu that pops up by long pressing any item on the Main interface.
- When you add a chat, if your target account has been added, it will prompt ""(target account)" This chat has been added, it is the "(current list of chats)" of the current list
- Added the ability to check for updates, which is located on the about interface.
- You can choose a picture in other apps to open it with the picture viewer of this app.
- Added the function of zooming in the picture viewer.
- Added "LoadingScreen" (loading interface), this interface is used to load part of the information of the logged-in account. The function is that the Main interface, Setting interface and Modify User Information interface after starting do not need to be loaded again, so that the application runs faster.
- Added "Developer Options" interface, which can be used to modify the server information, etc.
- The application will establish a connection to the server every time it starts the interface, and there is no need to establish it again afterwards, saving time.
- Many problems have been fixed to make the application more stable.


## alpha-6 (Dec. 27, 2020)
- Fixed an issue where users can add chat without login.
- Fixed the problem that there was no response when pressing the back button in the upper left corner of the picture viewer interface.
- Fixed the problem of incomplete display of the list in the about interface.
- You can choose to view the avatar by clicking on the avatar in the modify user information page.

## alpha-5 (Dec. 26, 2020)
- Change the "name" of the account in English (US) to "nickname".
- Click the avatar item on the modify user information page, you can choose to view the avatar or modify the avatar (not complete).
- Added a picture viewer, which can be used to view pictures. Use URLScheme to start the interface. Its URLScheme is mscr://picture_viewer/view?localPath= (local picture file path) or mscr://picture_viewer/view?url= (picture network address), where mscr can be changed to mschatroom, picture_viewer can be changed to pv.
- Fix the problem that Android 6.0 and above may not be able to obtain permissions.

## alpha-4 (Dec. 20, 2020)
- Change "Forget Password?" in English (US) to "Forgot Password?".
- Improved the main interface, you can add the chat, and every time you start you will get the name and avatar of each account.
- Deleted the intent-filter that can be launched directly on the desktop to modify the user information interface.

## alpha-3 (Dec. 17, 2020)
- Renamed the class "MainActivity" to "StartScreen".
- Fixed the problem that user information (avatar, name and what's up) might not be displayed when opening the settings.
- Fixed the problem of returning to the StartScreen (formerly MainActivity) interface when exiting.
- Change some Toast messages to Snackbar, which is more beautiful.
- You can view your account and email address on the modify user information page.
- Change the "电子邮箱" (email) of Simplified Chinese (China) to "邮箱地址" (email address).
- Fixed the serious problem that if user information and avatar were modified, the account with the same password would also be modified.
- Added about page.
- Changed the applied theme colors (colorPrimary, colorPrimaryDark and colorAccent).
- You can use URLScheme to launch the application.
- Added MainScreen (main interface) to add chat, this function is not yet perfect.

## alpha-2 (Dec. 7, 2020)
- Change the "Email" in English to "Email Address" to distinguish between email and email address.
- Add the function of using email address and CAPTCHA to login, which can be seen in the login interface.

## alpha-1 (Dec. 5, 2020)
- First version.