package mchorse.bbs.math;

import java.util.HashSet;
import java.util.Set;

/**
 * Operation enumeration
 * 
 * This enumeration provides different hardcoded enumerations of default 
 * math operators such addition, substraction, multiplication, division, 
 * modulo and power.
 */
public enum Operation
{
    ADD("+", 1)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a + b;
        }
    },
    SUB("-", 1)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a - b;
        }
    },
    MUL("*", 2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a * b;
        }
    },
    DIV("/", 2)
    {
        @Override
        public double calculate(double a, double b)
        {
            /* To avoid any exceptions */
            return a / (b == 0 ? 1 : b);
        }
    },
    MOD("%", 2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a % b;
        }
    },
    POW("^", 3)
    {
        @Override
        public double calculate(double a, double b)
        {
            return Math.pow(a, b);
        }
    },
    AND("&&", -3)
    {
        @Override
        public double calculate(double a, double b)
        {
            return isTrue(a) && isTrue(b) ? 1 : 0;
        }
    },
    OR("||", -3)
    {
        @Override
        public double calculate(double a, double b)
        {
            return isTrue(a) || isTrue(b) ? 1 : 0;
        }
    },
    SHIFT_LEFT("<<", 0)
    {
        @Override
        public double calculate(double a, double b)
        {
            return ((int) a) << ((int) b);
        }
    },
    SHIFT_RIGHT(">>", 0)
    {
        @Override
        public double calculate(double a, double b)
        {
            return ((int) a) >> ((int) b);
        }
    },
    BIT_AND("&", -1)
    {
        @Override
        public double calculate(double a, double b)
        {
            return ((int) a) & ((int) b);
        }
    },
    BIT_OR("|", -1)
    {
        @Override
        public double calculate(double a, double b)
        {
            return ((int) a) | ((int) b);
        }
    },
    BIT_XOR("^^", -1)
    {
        @Override
        public double calculate(double a, double b)
        {
            return ((int) a) ^ ((int) b);
        }
    },
    LESS("<", -2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a < b ? 1 : 0;
        }
    },
    LESS_THAN("<=", -2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a < b || equals(a, b) ? 1 : 0;
        }
    },
    GREATER_THAN(">=", -2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a > b || equals(a, b) ? 1 : 0;
        }
    },
    GREATER(">", -2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a > b ? 1 : 0;
        }
    },
    EQUALS("==", -2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return equals(a, b) ? 1 : 0;
        }
    },
    NOT_EQUALS("!=", -2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return !equals(a, b) ? 1 : 0;
        }
    };

    public static final Set<String> OPERATORS = new HashSet<>();

    public static boolean equals(double a, double b)
    {
        return Math.abs(a - b) < 0.00001;
    }

    public static boolean isTrue(double value)
    {
        return !equals(value, 0);
    }

    static
    {
        for (Operation op : values())
        {
            OPERATORS.add(op.sign);
        }
    }

    /**
     * String-ified name of this operation  
     */
    public final String sign;

    /**
     * Value of this operation in relation to other operations (i.e 
     * precedence importance)  
     */
    public final int value;

    private Operation(String sign, int value)
    {
        this.sign = sign;
        this.value = value;
    }

    /**
     * Calculate the value based on given two doubles 
     */
    public abstract double calculate(double a, double b);
}