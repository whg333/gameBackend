package com.why.game.util.operation;

public class OperationContext {

    private static ThreadLocal<Operation> operations = new ThreadLocal<Operation>();
    
    public static void recordOperation(Operation operation) {
    	//System.out.println(Thread.currentThread().getName()+" recordOperation...");
        operations.set(operation);
    }
    
    public static Operation get() {
        return operations.get();
    }
    
    public static void removeOperation() {
        Operation op = operations.get();
        if(op == null) {
            return;
        }
        
        op.ulock();
        operations.remove();
        //System.out.println(Thread.currentThread().getName()+" removeOperation...");
    }
    
}
