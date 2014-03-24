package com.example.giambiserver;

public class IllegalBankAccountException extends Exception{
    /**
     * 
     */
    private static final long serialVersionUID = -2071354699688629236L;
    public IllegalBankAccountException (String str){
        super(str);
    }
    public IllegalBankAccountException (){
        this("Bank account not found.");
    }
}
