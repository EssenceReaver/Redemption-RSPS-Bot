package com.john.RedemptionBotSDK.util;

import java.util.Stack;

public class Calculator {

    public static int calculate(String s) {
        Stack<Integer> number = new Stack<>();
        Stack<Character> op = new Stack<>();
        int i = 0, n = s.length();

        while (i < n) {
            char c = s.charAt(i);

            if (!Character.isDigit(c) && c != '+' && c != '-' && c != '*' && c != '/' && c != 'x'){
                i++;
                continue;
            }

            if (Character.isDigit(c)) {
                int num = 0;
                while (i < n && Character.isDigit(s.charAt(i))) {
                    num = num * 10 + (s.charAt(i) - '0');
                    i++;
                }
                number.push(num);
                continue;
            }

            while (!op.isEmpty() && prec(op.peek()) >= prec(c)) {
                int n2 = number.pop();
                int n1 = number.pop();
                number.push(res(n1, n2, op.pop()));
            }

            op.push(c);
            i++;
        }

        while (!op.isEmpty()) {
            int n2 = number.pop();
            int n1 = number.pop();
            number.push(res(n1, n2, op.pop()));
        }

        return number.pop();
    }

    public static int res(int n1, int n2, char c) {
        return switch (c) {
            case '+' -> n1 + n2;
            case '-' -> n1 - n2;
            case '*' -> n1 * n2;
            case 'x' -> n1 * n2;
            case '/' -> n1 / n2;
            default -> 0;
        };
    }

    public static int prec(char c) {
        return switch (c) {
            case '+', '-' -> 1;
            case '*', '/', 'x' -> 2;
            default -> 0;
        };
    }
}
