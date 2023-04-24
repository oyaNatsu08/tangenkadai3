package tangenkadai3;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Calculator {
    //数値の桁数分処理を飛ばすための変数
    static int count = 0;
    //入力した計算式を格納
    static String[] calculator;
    //逆ポーランド記法にするために演算子を格納
    static Stack<String> stack;
    //逆ポーランド記法に変換したものを格納
    static ArrayList<String> rpn;

    public static void main(String[] args) {
        System.out.println("計算式を入力してください：");
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        calculator = str.split("");

        //逆ポーランド記法にするために演算子を格納
        stack = new Stack<String>();

        //逆ポーランド記法に変換したものを格納
        rpn = new ArrayList<>();

        //数式を逆ポーランド記法に変換
        createRpn();
        System.out.println(rpn);

        //逆ポーランド記法をもとに計算式を演算
        int result = calculate();
        System.out.println("結果：");
        System.out.println(result);
    }

    //計算式を逆ポーランド記法に変換する処理
    public static void createRpn() {

        //スタックの中身を一時避難場所
        Stack<String> stackTmp = new Stack<>();
        for (int i = 0; i < calculator.length; i++) {
            //countがマイナスのとき、0を代入
            if (count < 0) {
                count = 0;
            }

            //parentheses(i, stackTmp);

            if ("(".equals(calculator[i])) {
                //スタックの中身を一時避難
                while (!stack.isEmpty()) {
                    stackTmp.push(stack.pop());
                }

                // ) が来るまでループを回す
                while (!(")".equals(calculator[i]))) {
                    changeRpn(i);
                    i++;
                }
                //スタックの残りの中身を全部リストにadd
                while (!stack.isEmpty()) {
                    //スタックの中身をリストにadd
                    String tmp = stack.pop();
                    rpn.add(tmp);
                }

                //stackTmpに渡していた演算子をstackに戻す
                while (!stackTmp.isEmpty()) {
                    stack.push(stackTmp.pop());
                }

            } else {
                changeRpn(i);
            }
        }

        //スタックの中身が空になるまでリストrpnにポップ
        while (!stack.isEmpty()) {
            String tmp = stack.pop();
            rpn.add(tmp);
        }
    }

    //計算式が()を再起処理するため→(1 * (2 + 3) )などのため
//    public static void parentheses(int i, Stack<String> stackTmp) {
//        // ()がどうか判定
//        if ("(".equals(calculator[i])) {
//            i++;
//            //System.out.println("i = " + i);
//            //スタックの中身を一時避難
//            while (!stack.isEmpty()) {
//                stackTmp.push(stack.pop());
//            }
//
//            // ) が来るまでループを回す
//            while (!(")".equals(calculator[i]))) {
//                //( なら 再起処理
//                if("(".equals(calculator[i])) {
//                    parentheses(i, stackTmp);
//                }
//                changeRpn(i);
//                i++;
//            }
//            //スタックの残りの中身を全部リストにadd
//            while (!stack.isEmpty()) {
//                //スタックの中身をリストにadd
//                String tmp = stack.pop();
//                rpn.add(tmp);
//            }
//
//            //stackTmpに渡していた演算子をstackに戻す
//            while (!stackTmp.isEmpty()) {
//                stack.push(stackTmp.pop());
//            }
//
//        } else {
//            changeRpn(i);
//        }
//    }

    //優先順位をもとに逆ポーランド記法に変換
    public static void changeRpn(int i) {
        //数値かどうか判定 かつ 前回が数値ではないか
        if (calculator[i].matches("[0-9]") && count <= 0) {
            //2桁以上の値を考慮して格納
            String digitNum = checkDigit(i);
            count = digitNum.length();
            rpn.add(digitNum);
        } else if ("+".equals(calculator[i]) || "-".equals(calculator[i]) || "*".equals(calculator[i]) || "/".equals(calculator[i])) { //演算子かどうか判定
            if (stack.isEmpty()) {
                stack.push(calculator[i]);
            } else {
                //演算子を重み変換(配列で参照しているもの)
                int weight = changeWeight(calculator[i]);

                //ループを回して演算子がスタックからなくなるまでやる
                while (!stack.isEmpty()) {
                    //スタックにある値をポップ
                    String operator = stack.pop();
                    //演算子を重み変換(スタック内にあったもの)
                    int weight2 = changeWeight(operator);

                    //スタックにあった演算子と配列で参照している演算子を比較
                    if (weight > weight2) {
                        stack.push(operator);
                        stack.push(calculator[i]);
                        break;
                    } else if (weight <= weight2) {
                        rpn.add(operator);
                    }
                }
                //スタックが空になったらプッシュする
                if (stack.isEmpty()) {
                    stack.push(calculator[i]);
                }
            }
        }

        //桁数のカウントを-1
        count--;

    }

    //演算子の重みを設定→優先順位を判断するため
    public static int changeWeight(String operator) {
        int weight = 0;
        if("+".equals(operator) || "-".equals(operator)) {
            weight += 1;
        } else if ("*".equals(operator) || "/".equals(operator)) {
            weight += 2;
        }
        return weight;
    }

    //2桁以上の数値を計算する処理
    public static String checkDigit(int i) {
        //桁数を考慮した値
        String digitNum = "";
        while (calculator[i].matches("[0-9]")) {
            digitNum += calculator[i];
            i++;

            //配列の末尾の場合はループの外に
            if (i >= calculator.length) {
                break;
            }
        }
        return digitNum;
    }

    //逆ポーランド記法をもとに計算式を計算する処理
    public static int calculate() {
        int result = 0;
        int a = 0;
        int b = 0;
        Stack<Integer> stackNum = new Stack<>();

        for (int i = 0; i < rpn.size(); i++) {
            //数値かどうかを判定
            if (rpn.get(i).matches("[0-9]+")) {
                stackNum.push(Integer.parseInt(rpn.get(i)));
                System.out.println(rpn.get(i));
            } else if ("+".equals(rpn.get(i))) {
                a = stackNum.pop();
                b = stackNum.pop();
                result = a + b;
                stackNum.push(result);
            } else if ("-".equals(rpn.get(i))) {
                a = stackNum.pop();
                b = stackNum.pop();
                result = b - a;
                stackNum.push(result);
            } else if ("*".equals(rpn.get(i))) {
                a = stackNum.pop();
                b = stackNum.pop();
                result = a * b;
                stackNum.push(result);
            } else if ("/".equals(rpn.get(i))) {
                a = stackNum.pop();
                b = stackNum.pop();
                result = b / a;
                stackNum.push(result);
            }
        }
        return result;
    }
}