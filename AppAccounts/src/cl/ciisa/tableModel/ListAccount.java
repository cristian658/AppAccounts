package cl.ciisa.tableModel;
 
public class ListAccount {
    protected long id;
    protected String name;
    protected String type;
    protected long price;
    
    public ListAccount(){
    	this.id=0;
    	this.name="";
    	this.type="";
    	this.price=0;
    }
    
    public ListAccount(long id_, String name_,String type_,long price_){
    	this.id=id_;
    	this.name=name_;
    	this.type=type_;
    	this.price=price_;
    }
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}
}
