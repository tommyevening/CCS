import java.util.HashMap;
import java.util.Map;

class MathOperationFactory {
    private MathOperations operations = new MathOperations();
    private Map<String, String> operationMap = new HashMap<>();

    public MathOperationFactory() {
        operationMap.put("ADD", "add");
        operationMap.put("SUB", "subtract");
        operationMap.put("MUL", "multiply");
        operationMap.put("DIV", "divide");
    }

    public int performOperation(String operationType, int a, int b) throws Exception {
        String methodName = operationMap.get(operationType);
        if (methodName == null) {
            throw new IllegalArgumentException("Nieznana operacja: " + operationType);
        }

        return (int) MathOperations.class
                .getMethod(methodName, int.class, int.class)
                .invoke(operations, a, b);
    }
}