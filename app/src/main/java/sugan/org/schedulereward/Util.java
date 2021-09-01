package sugan.org.schedulereward;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Util {



    static String today(){
        return new SimpleDateFormat("yyMMdd").format(new Date());
    }

    static boolean checkFrontBlank(CharSequence s){

        if (s.toString().startsWith(" ")) return true;
        return false;
    }

    static void editTextSaveListener(CharSequence s, TextView save, TextView title) {

        if(checkFrontBlank(s)) {
            title.setText(s.toString().substring(1));   //중간 공백을 허용하기 위해. to admit blanks in the middle of title.
            return;
        }
        if(s.toString().length()>0) {
            //  link_noted = true;
            //if(link_noted && scored)
            save.setVisibility(View.VISIBLE);
        }
        else { //link_noted = false;
            save.setVisibility(View.GONE);     }

    }

    static int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());

    }

    static void setViewWidth(View v, Context context, float widthDip ) {

    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(v.getLayoutParams());
    param.width = dip2px(context, widthDip);
    v.setLayoutParams(param);

    }


    static void setViewWidthAndMarginright(View v, Context context, float widthDip ) {

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(v.getLayoutParams());
        param.width = dip2px(context, widthDip);
        param.setMargins(0,0, dip2px(context, 10),0);
        v.setLayoutParams(param);

    }
   /*  1. search parenthesis
       2. divide by + and -
       3. calculate * and /
    */
   static double calculate(String formula) {
       String f = formula;
       double rd = 0;
       String rs = "";
       int  left_parentheses = f.indexOf('(');
       int left_position = 0;
       int right_parentheses = 0;

       while(left_parentheses != -1) {

           for(int i = 1 ; i<f.length();i++ ){
               if(f.charAt(i) == '(') left_parentheses = i;
               else if(f.charAt(i) == ')') {
                   Log.i("left_parenthes = " , left_parentheses + " ");
                       right_parentheses = i; Log.i("right_parenthes = " , i + " ");
                       break ;
               }
           }

           String parentheses_formula = f.substring(left_parentheses+1, right_parentheses) ;
            Log.i("parentheses_formula", parentheses_formula);
           rs = calcul_r(parentheses_formula) +"";
           Log.i("rs =" , rs + " ");
           f = f.replace("("+parentheses_formula+")", rs+"");
           Log.i("replaceAll =" , f + " ");



           left_parentheses = f.indexOf('(');

       }
       Log.i("f = ", f);
       double r = calcul_r(f);
       Log.i("r = ", r + " ");

       return r;

   }
   
    static double calcul_r (String formula) {  // 18-3*6/2
        String f = formula.trim();
        double r = 0;
        //if(f.equals(""))
        if(!(f.startsWith("+")||f.startsWith("-"))) {
            f = "+"+f;
        }
        int ss = 0;
        int se = 0;
        ArrayList<String> f_unit = new ArrayList<>(5);
        for(int i=1; i<f.length();i++) {
            if(f.charAt(i)=='+' || f.charAt(i) =='-')  {
                se = i;
                f_unit.add(f.substring(ss, se));
                ss = i;
            }
        }
        f_unit.add(f.substring(se, f.length()));

        for(int i=0; i<f_unit.size();i++){
            Log.i("formula_unit" + i, f_unit.get(i)  );
            r = r + calcul_r2(f_unit.get(i));
            Log.i("formula_unit_r" + i, r + " " );

        }
        return r;
    }

    static double calcul_r2(String formula){   //+3*8/2
        String f = formula.substring(1).trim();
        int s = 0;
        ArrayList<String> f_unit = new ArrayList<>(5);
        for(int i=1; i<f.length(); i++) {
            if(f.charAt(i)=='*' || f.charAt(i) =='/'){
                f_unit.add(f.substring(s, i));
                s = i;
            }
        }
        f_unit.add(f.substring(s, f.length()));
        double r = Double.parseDouble(f_unit.get(0).trim());

        for(int i=1; i<f_unit.size();i++) {
            String f1 = f_unit.get(i);
            Log.i("formula_unit" + i, f1 );
            if(f1.startsWith("*")) {
                f1 = f1.substring(1).trim();
                r = r * Double.parseDouble(f1);
            }
            else {
                f1 = f1.substring(1).trim();
                r = r / Double.parseDouble(f1);
            }
        }
        if(formula.startsWith("-")) r = -r;
        return r;

    }

    static void setLinearWeightParam(int weight, View view, int layout_id){
        LinearLayout layout = view.findViewById(layout_id);
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams)layout.getLayoutParams();
        param.weight = weight;
        layout.setLayoutParams(param);
    }
}

class Boolean_wrapper {   //Boolean 은 값을 변경할 수 없는 객체(like string)이므로 이런 클래스를 만듬.
    boolean value = false;
}
