package com.daming.multithreading.test;

public class Tes {

    public static void main(String[] args) {
        child child = new child();
        boolean res = child.test2();
        System.out.println(res);

        parent parent = new parent();
        boolean res2 = parent.test2();
        System.out.println(res2);
    }

}

class parent{
    protected boolean test(){
        throw new UnsupportedOperationException();
    }

    public boolean test2(){
        if (test()){
            return false;
        }else {
            return true;
        }
    }
}

class child extends parent{
    @Override
    protected boolean test() {
        return true;
    }
}
