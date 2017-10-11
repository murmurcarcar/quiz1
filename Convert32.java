public class Convert32
{
    public static void main(String[] args)
    {    
        int[] bits = getInput(args[0]);
        System.out.println("jave Convert32 " + args[0]);
        convertBinary(bitPattern(bits));
        convertHex(bitPattern(bits));
        convertFloat(bitPattern(bits));
        convertInt(bitPattern(bits));

    }
    
  
    private static int[] bitPattern(int[] bits)
    {
        int[] bitPattern = new int[bits.length];
        for (int i = 0; i < bits.length; i++)
            {bitPattern[i] = bits[i];}
        return bitPattern;
    }

 
    private static int[] getInput(String str)
    {
        if (str.startsWith("0b") || str.startsWith("0B"))
        {
            return sBinary(str.substring(2));
        }
        else if (str.startsWith("0x") || str.startsWith("0X"))
        {
            return sHex(str.substring(2));
        }
        else if (str.indexOf('.') < 0)
        {
            return sInt(Integer.parseInt(str));
        }
        else
        {
            return sFloat(Float.parseFloat(str));
        }
        
    }
    
    private static int[] sBinary(String bin)
    {
        int[] result = new int[32];
        while (bin.length() < 32)
        {
            bin = "0" + bin;
        }
        
        for (int i = 0; i < 32; i++)
        {
            result[i] = bin.charAt(i) - '0';
        }
        return result;
    }

    private static int[] sHex(String hex)
    {
        int[] result = new int[32];
        while (hex.length() < 8)
        {
            hex = "0" + hex;
        }

        for (int i = 0; i < 8; i++)
        {
            char ch = hex.charAt(i);
            ch = Character.toUpperCase(ch);
            int value = ch - '0';
            if (value < 0 || value >= 10)
                value = ch - 'A' + 10;

            for (int j = 0; j < 4; j++)
            {
                result[i * 4 + 3 - j] = value % 2;                    
                value = value / 2;
            }                
        }
        return result;
    }
    
    private static int[] sInt(int val)
    {
        int[] result = new int[32];
        boolean neg = val < 0;        
        
        val = Math.abs(val);
        
        for (int i = 31; i >= 0; i--)
        {
            result[i] = val % 2;
            val = val / 2;
        }
        
        if (neg)
            twoComplete(result);

        return result;
    }
    
    private static int[] sFloat(float val)
    {
        int[] result = new int[32];
        result[0] = 0;
        if (val < 0)
        {
            val = -val;
            result[0] = 1;
        }
        
        // find exponent
        int exp = 0;
        while (val >= 2)
        {
            exp++;
            val /= 2;
        }
        while (val < 1)
        {
            exp--;
            val *= 2;
        }        
        exp += 127;
        
        for (int i = 0; i < 8; i++)
        {
            result[8 - i] = exp % 2;
            exp /= 2;
        }
        
        val -= 1;
        for (int i = 0; i < 23; i++)
        {
            val *= 2;
            if (val >= 1)
            {
                result[i + 9] = 1;
                val -= 1;
            }
            else
            {
                result[i + 9] = 0;
            }
        }
        
        return result;
    }
    
    private static void twoComplete(int[] bits)
    {
        for (int i = 0; i < bits.length; i++)
            bits[i] = 1 - bits[i];
        
     
        int carry = 1;
        for (int i = 31; i >= 0 && carry > 0; i--)
        {
            int val = bits[i] + carry;
            bits[i] = val % 2;
            carry = val / 2;
        }
    }
    
   
    private static void convertBinary(int[] bits)
    {
        System.out.print("binary: 0B");
        
        boolean printed = false;
        for (int i = 0; i < bits.length; i++)
        {
            if (bits[i] > 0 || printed || i == bits.length - 1)
            {
                System.out.print(bits[i]);
                printed = true;
            }
        }
        System.out.println();
    }
    
    
    private static void convertHex(int[] bits)
    {
        boolean printed = false;
        System.out.print("hex: 0X");
        for (int i = 0; i < 8; i++)
        {
            int val = 0;
            for (int j = 0; j < 4; j++)
            {
                val = val * 2 + bits[i * 4 + j];
            }
            
            if (val > 0 || printed || i == 7)
            {
                if (val >= 10)
                    System.out.print((char)(val - 10 + 'A'));
                else
                    System.out.print(val);
                printed = true;
            }
        }

        System.out.println();
    }
    
    private static void convertInt(int[] bits)
    {
        boolean neg = bits[0] == 1;
        
        if (neg)
            twoComplete(bits);
        
        int val = 0;
        for (int i = 0; i < bits.length; i++)
            val = val * 2 + bits[i];
        
        if (neg)
            val = -val;
        
        System.out.println("int: " + val);        
    }
    
    private static void convertFloat(int[] bits)
    {
        int sign = 1;        
        if (bits[0] == 1)
            sign = -1;

        System.out.print("float: ");

        boolean allzero = true;
        boolean allexp1 = true;
        boolean allfrac0 = true;
        for (int i = 1; i < bits.length; i++)
        {   
            if (bits[i] != 0)
            {
                allzero = false;
                if (i >= 9)
                    allfrac0 = false;
            }
            else
            {
                if (i < 9)
                    allexp1 = false;
            }
        }
        
        if (allzero)
        {
            System.out.println("Zero");
            return;
        }
        
        if (allexp1)
        {
            if (bits[0] == 1)
                System.out.print("-");
            else
                System.out.print("+");
            
            if (allfrac0)
                System.out.println("INF");
            else
                System.out.println("NaN");
            return;
        }
        
        int exp = 0;
        for (int i = 1; i < 9; i++)
        {
            exp = exp * 2 + bits[i];
        }
        exp -= 127;
        
        float value = 1.0f;
        float part = 1f;
        
        for (int i = 9; i < 32; i++)
        {
            part /= 2;
            if (bits[i] == 1)
                value += part;
        }
        
        while (exp > 0)
        {
            value *= 2;
            exp--;
        }
        while (exp < 0)
        {
            value /= 2;
            exp++;
        }
        
        System.out.println(value * sign);
    }
}
