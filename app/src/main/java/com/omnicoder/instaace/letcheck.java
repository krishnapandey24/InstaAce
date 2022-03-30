package com.omnicoder.instaace;

// Online Java Compiler
// Use this editor to write, compile and run your Java code online

class letcheck {
    public static int romanToInt(String s) {
        int size=s.length();
        int count=0;
        char[] ch = s.toCharArray();
        int[] romans =new int[size];
        for(int i=0;i<size;i++){
            char c=ch[i];
            switch(c){
                case 'I':
                    romans[i]=1;
                    break;
                case 'V':
                    romans[i]=5;
                    break;
                case 'X':
                    romans[i]=10;
                    break;
                case 'L':
                    romans[i]=50;
                    break;
                case 'C':
                    romans[i]=100;
                    break;
                case 'D':
                    romans[i]=500;
                    break;
                case 'M':
                    romans[i]=1000;
                    break;

            }
        }
        boolean skip=false;
        int E=size-1;
        for(int i=0;i<size;i++){
            if(skip){
                skip=false;
                continue;
            }
            int num = romans[i];
            if(i==E){
                count=count+num;
            }else {
                int next = romans[i + 1];
                if (num < next) {
                    count = count + (next - num);
                    skip = true;
                } else {
                    count = count + num;
                }
            }
        }
        return count;

    }

    public static void main(String[] args) {
        System.out.println("Hello, World!");
        System.out.println(romanToInt("DCCCL"));
    }
}
