package com.mrshiehx.mschatroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import android.app.AlertDialog;

import com.mrshiehx.mschatroom.utils.AccountUtils;
import com.mrshiehx.mschatroom.utils.FormatTools;
import com.mrshiehx.mschatroom.xml.user_information.User;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//没用的，用来做测试的
public class PETest extends Activity {
    EditText num, pas;
    Button sav, rea, thi;
    ImageView ima;
    int yourChoice, nowGender;
    private List<User> userInformationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.petest);
        initData();
        CreateXML();
        test();
        a();

        num = findViewById(R.id.num);
        pas = findViewById(R.id.pas);
        sav = findViewById(R.id.sav);
        rea = findViewById(R.id.rea);
        thi = findViewById(R.id.third);
        ima = findViewById(R.id.ima);
        sav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    pas.setText(EncryptText.encrypt(num.getText().toString(),"aaa"));
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }*/

                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final AccountUtils accountUtilsa=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);;
                        ConnectionUtils jdbcUtil=new ConnectionUtils(Variables.SERVER_ADDRESS);
                        Connection conn=jdbcUtil.getConnection(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD);

                        String email= null;
                        try {
                            try{
                            InputStream ins=accountUtilsa.getUser(PETest.this, EnDeCryptTextUtils.encrypt(email + Variables.SPLIT_SYMBOL + "asdf", Variables.TEXT_ENCRYPTION_KEY));


                                File file = new File(Environment.getExternalStorageDirectory().getPath());

//将file转换成InputStream对象

//将file数据流写进databaseFilename文件
                                FileOutputStream fos = new FileOutputStream("/sdcard/xx.xml");
                                byte[] buffer = new byte[8192];
                                int count = 0;
                                while ((count = ins.read(buffer)) > 0) {
                                    fos.write(buffer, 0, count);
                                }
                                fos.close();
                                ins.close();

                            }catch (IOException e){
                                Looper.prepare();
                                Toast.makeText(PETest.this, ""+e, Toast.LENGTH_SHORT).show();
                                Looper.loop();

                                e.printStackTrace();
                            }








                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        }
                        AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);;
                        Looper.prepare();
                        Toast.makeText(PETest.this, accountUtils.getAccountByEmail(PETest.this,"573E2C51779ECD7B6C9873B0F78C7CC4CCC91AAD0294B87A"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();


                */

                /*InputStream is= null;
                try {
                    is = getResources().getAssets().open("ace.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String str = null;
                /*try {
                    str = IOUtils.toString(is, "utf-8");
                    System.out.printf("its:"+str);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/


                /*try {
                    byte[] bytes = new byte[0];
                    bytes = new byte[is.available()];
                    is.read(bytes);
                    System.out.println("输出:"+bytes );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    StringBuilder sb = new StringBuilder();
                    String line;

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    String str2 = sb.toString();
                    System.out.println("输出2:" + str2);
                }catch (IOException e){
                    e.printStackTrace();
                }*/
                /*final ProgressDialog ing=new ProgressDialog(PETest.this);
                ing.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {InputStream is= null;
                        try {
                            is = getResources().getAssets().open("userInformation.xml");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);;

                        accountUtils.uploadUser(PETest.this,ing,"38280D93AE3033B05697894046BAFCBD",is);

                    }
                }).start();*/


                a();
            }
        });
        rea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            /*try {
                num.setText(EncryptText.decrypt(pas.getText().toString(),"aaa"));
            } catch (Exception e) {
                Log.e("vvvvvvvvvvvv",""+e);
                e.printStackTrace();
            }*/
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);;
                    Looper.prepare();
                    Toast.makeText(PETest.this, accountUtils.getEmailByAccount(PETest.this,"F864B3BA990F36A2"), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }).start();*/





            /*final ProgressDialog ing=new ProgressDialog(PETest.this);
            ing.show();
            new Thread(new Runnable() {
                @Override
                public void run() {InputStream is= null;
                    try {
                        is = getResources().getAssets().open("userInformation2.xml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    AccountUtils accountUtils=new AccountUtils(Variables.DATABASE_NAME,Variables.DATABASE_USER,Variables.DATABASE_PASSWORD,Variables.DATABASE_TABLE_NAME);;

                    accountUtils.uploadUser(PETest.this,ing,"38280D93AE3033B05697894046BAFCBD",is);

                }
            }).start();*/


            }

            ;
        /*String soureTxt = "qqqqqq&dssdff";
        String key = EncryptText.KEY;
        String str = null;
        System.out.println("明文：" + soureTxt);
        try {
            str = EncryptText.encrypt(soureTxt, key);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        System.out.println("加密：" + str);
        try {
            System.out.println("解密：" + EncryptText.decrypt(str, key));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        System.out.println(str + "====" + key);*/
        });
        thi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final AccountUtils au = new AccountUtils(Variables.DATABASE_NAME, Variables.DATABASE_USER, Variables.DATABASE_PASSWORD, Variables.DATABASE_TABLE_NAME);
                        final InputStream is = au.getInputStream(PETest.this, "avatar", "password", "38280D93AE3033B05697894046BAFCBD");
                        if (is != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ima.setImageDrawable(FormatTools.getInstance().InputStream2Drawable(is));
                                }
                            });
                        } else {
                            Looper.prepare();
                            Toast.makeText(PETest.this, "fuckingempty", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();
            }
        });
    }


    void a() {
        final String[] items = {"我是1", "我是2", "我是3", "我是4"};
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(PETest.this);
        singleChoiceDialog.setTitle("我是一个单选Dialog");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, nowGender,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                        nowGender = which
                        ;
                        dialog.dismiss();
                        Toast.makeText(PETest.this,
                                "你选择了" + String.valueOf(nowGender),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        singleChoiceDialog.setPositiveButton(getResources().getString(android.R.string.cancel), null);
        singleChoiceDialog.show();
    }

    private void test() {
        for (User newsInfo : this.userInformationList) {  /*类型是遍历数据的类型 每次遍历集合元素名字  ：遍历数组或list*/
            System.out.println(newsInfo.toString());
        }
    }

    private void initData() {
        userInformationList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            User newsInfo = new User();
            newsInfo.setTitle("Title" + i);
            newsInfo.setContent("jjjjj" + i);
            /*获得日期 */
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss");
            String time = dateFormat.format(date);
            newsInfo.setDate(time + ";" + i);
            /*newsinfo装进来*/
            userInformationList.add(newsInfo);
        }
    }

    public void CreateXML() {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "news.xml");
        try {
            FileOutputStream fos = new FileOutputStream(file);/*fos输出流 file路径*/
            xmlSerializer.setOutput(fos, "utf-8");/*utf_8写流编码方式*/
            xmlSerializer.startDocument("utf-8", true);/*utf_8 xml显示的编码方式,xml和别的xml有无联系*/
            xmlSerializer.startTag(null, "newss");   /*命名空间有无*/
            for (User newsInfo : this.userInformationList) {
                xmlSerializer.startTag(null, "news content=\"" + "xxx" + "\"/");
                xmlSerializer.startTag(null, "news2 content=\"" + "xxx" + "\"/");
                xmlSerializer.startTag(null, "title");
                xmlSerializer.text(newsInfo.getTitle());
                xmlSerializer.endTag(null, "title");
                xmlSerializer.startTag(null, "content");
                xmlSerializer.text(newsInfo.getContent());
                xmlSerializer.endTag(null, "content");
                xmlSerializer.startTag(null, "date");
                xmlSerializer.text(newsInfo.getDate());
                xmlSerializer.endTag(null, "date");
                xmlSerializer.endTag(null, "news");
            }
            xmlSerializer.endTag(null, "newss");
            xmlSerializer.endDocument();
            fos.close();
            Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show();
            return;        /*方法结束标志*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "FALLED", Toast.LENGTH_SHORT).show();
    }
}
