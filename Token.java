package ba55_compiler;
//The class from which the tokens are created
public class Token<TT, TV> {
	
	private final TT type;
    private final TV value;
	
	public Token(TT type, TV value) {
        this.type = type;
        this.value = value;
    }

	public String toString() {
        return "<"+this.type+","+this.value+">";
    }
	
	public TT getType(){
		return this.type;
	}
	
	public TV getValue() {
		return this.value;
	}
}
