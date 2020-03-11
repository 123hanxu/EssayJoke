package com.example.baselibrary;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.widget.Toast.LENGTH_SHORT;

public class ViewUntil {

    //前期
    public static void inject(Activity activity){
        inject(new ViewFinder(activity),activity);
    }

    //后期
    public static void inject(View view){
        inject(new ViewFinder(view),view);
    }

    //后期
    public static void inject(View view,Object object){
        inject(new ViewFinder(view),object);
    }

    //兼容上面三個方法  object反射需要执行的类
    public static void inject(ViewFinder viewFinder,Object object){
        injectFiled(viewFinder,object);
        injectEvent(viewFinder,object);

    }


    /**
     *事件注入
     */
    private static void injectEvent(ViewFinder viewFinder, Object object) {

        //1.获取类里面所有的方法
        Class<?> cls=object.getClass();
        Method[] methods=cls.getDeclaredMethods();
        //2.获取onClick的里面的value值
        for(Method method:methods){
           OnClick onClick= method.getAnnotation(OnClick.class);
           if(onClick!=null){
               //3.findViewById找到view
               int[] viewIds=onClick.value();
               if(viewIds.length>0){
                   for(int viewId:viewIds){
                       View view=viewFinder.findViewById(viewId);

                       boolean isCheckNet=method.getAnnotation(CheckNet.class)!=null;

                       //4.view.setOnClickListen
                       if(view!=null){
                           view.setOnClickListener(new DeclaredOnClickListener(method,object,isCheckNet));
                       }

                   }

               }
           }

        }
    }

    private static class DeclaredOnClickListener implements View.OnClickListener {

        public Method method;
        public Object object;
        boolean isCheckNet;

        public DeclaredOnClickListener(Method method, Object object, boolean isCheckNet) {
            this.method = method;
            this.object = object;
            this.isCheckNet=isCheckNet;
        }

        @Override
        public void onClick(View v) {
           //需不需要该网络
            if(isCheckNet){
                if(networkAvailable(v.getContext())) {
                    //写死有问题 需要配置
                    Toast.makeText(v.getContext(), "请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            try {
                //5.反射执行该方法
                method.setAccessible(true);
                method.invoke(object,v);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    method.invoke(object,null);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    private static void injectFiled(ViewFinder viewFinder, Object object) {
        //1.获取类里面所有的属性
        Class<?> cls=object.getClass();
        //获取所有的属性包括私有和公有
        Field[] fields=cls.getDeclaredFields();


        //2、获取viewById的里面的value值
        for(Field field : fields){
            ViewById viewById = field.getAnnotation(ViewById.class);
            if(viewById!=null){
                //获取ViewById的属性值
                int viewId=viewById.value();
                //3. 通过findViewById获取view
                View view=viewFinder.findViewById(viewId);


                //4. 动态的注入找到的view
                if(view!=null){
                    //通过反射将属性注入到对象中,
                    //设置所有属性都能注入包括私有和公有
                    field.setAccessible(true);
                    try {
                        field.set(object,view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }else {
                throw new RuntimeException("Invalid @ViewInject for "
                        + cls.getSimpleName() + "." + field.getName());
            }
        }
    }

    private static boolean networkAvailable(Context context){
        //得到连接管理器对象
        try{
            ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();

            if(networkInfo!=null && networkInfo.isConnected()){
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
