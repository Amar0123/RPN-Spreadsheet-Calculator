import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class Spreadsheet {
    
    //store processed lines from spreadsheet
    static ArrayList<Object> lines = new ArrayList<Object>();
    //store processed values with a a index to retrieve easily
    static Hashtable ssIndexTable = new Hashtable();
    
    public static void main(String args[]) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        //store line from spreadsheet
        String line = null;
        
        //dimensions of spreadsheet
        int height = 0;
        int width = 0;

        //keep track of current indexes
        int widthIndex = 1;
        char heightIndex = 'A';   
        int counter = 1; //for 
        
        //handle first line(width and height)
        try {
            line = reader.readLine();
            if(line != null){
                String[] widthNheight = line.split("\\s+");
                width = Integer.parseInt(widthNheight[0]);
                height = Integer.parseInt(widthNheight[1]);
                lines.add(line);
            }else{
                System.out.println("Input file is empty or Missing");
                System.exit(10);
            }    
        }catch(IOException e){
            e.printStackTrace();
        }
        
        //handle expressions(first round)
        try {
            while((line = reader.readLine()) != null){
                Double output = processLine(line);
                if(output != null){
                    lines.add(output);
                    ssIndexTable.put(String.valueOf(heightIndex)+String.valueOf(widthIndex),output);
                }else{
                    lines.add(line);
                }

                widthIndex = counter % width + 1;
                if ( widthIndex ==  1 ){
                    heightIndex++; // increment alphabet when finish iterating width  
                }
                counter++;
            }    
        }catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
        
        //reset index for iterating through remaining expressions of spreadsheet
        widthIndex = 1;
        heightIndex = 'A';
        
        //handle remaining expressions(due to dependencies)
        for(int i = 1;i<lines.size();i++){
            counter = 1;
            heightIndex = 'A';
            for(int j = 1;j<lines.size();j++){
                if(lines.get(j) instanceof Double){// if its a double means processing done
                    widthIndex = counter % width + 1;
                    if(widthIndex == 1){
                        heightIndex++;
                    }
                    counter++;
                    continue; //already processed in to a single value so can move on
                }
                
                String valueString = lines.get(j).toString();
                Double value = processLine(valueString);
                if(value != null){
                    lines.set(j,value); // replace previous statment with processed value
                    //update table for usage at other expressions with dependencies
                    ssIndexTable.put(String.valueOf(heightIndex)+String.valueOf(widthIndex),value);
                }
                
                widthIndex = counter % width + 1;
                if(widthIndex == 1){
                    heightIndex++;
                }
                counter++;
            }
        }
        
        //all Done
        System.out.println(lines.get(0));
		for ( int i=1; i < lines.size(); i++ ) {
            if ( lines.get(i) instanceof Double) { 
                System.out.println(String.format("%.5f",lines.get(i)));        
            }else{
                System.out.println("Cyclical Dependency present");
                System.exit(10);
            }
		}
    }
    
    //parse line for processing
    public static Double processLine(String input){
        String[] values = input.split("\\s+");
        Stack<Double> stack = new Stack<Double>();
        
        for(String value : values){
            if(isInteger(value)){
                double validValue = Double.parseDouble(value);
                stack.push(validValue);
            }else if(isOperand(value)){
                double value1 = stack.pop();
                double value2 = stack.pop();
                double result = calculater(value2,value1, value);//value2 before value1 because stack reverses the order
                stack.push(result);
            }else if(value.trim().length() > 0){
                if(ssIndexTable.get(value) != null){
                    Object temp = ssIndexTable.get(value);
                    stack.push((Double)temp);// push back because expression might have more than 1 operator
                }else{
                    return null;
                }
            }
        }
        return stack.pop();// all operator for the expression done 
    }
    
    //calculate expressions only for (+-*/)
    public static Double calculater(double num1, double num2, String operand){
		if(operand.equals("+")){
            return num1+num2;
        }else if(operand.equals("-")){
            return num1-num2;
        }else if(operand.equals("*")){
			return num1*num2;
        }else{
            return num1/num2;
        }
	}
    
    //check if integer or somethingelse
    public static boolean isInteger(String value){
        String numbers = ".0123456789-";
        for( int i = 0;i < value.length();i++){
			if(numbers.contains(value.charAt(i)+"") == false){
                return false;
            }
		}
		return true;
    }
    
    //check if input is an operand(+-*/)
    public static boolean isOperand(String operand){
		if (operand.length() != 1 ){
            return false;
        }else{
            return 	"+-*/".contains(operand);		
        }
	}
}