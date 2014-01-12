package cl.ciisa.tableModel;

import java.util.Date;

public class Capital {
	
	private int id;
	private int id_user;
	private int tot_capital;
	private String created;
	private String updatedCap;
	private int state;
	private int synchronize;
	
	public int getId() {
		return id;
	}
	public int getId_user() {
		return id_user;
	}
	public void setId_user(int id_user) {
		this.id_user = id_user;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTot_capital() {
		return tot_capital;
	}
	public void setTot_capital(int tot_capital) {
		this.tot_capital = tot_capital;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getUpdatedCap() {
		return updatedCap;
	}
	public void setUpdatedCap(String updatedCap) {
		this.updatedCap = updatedCap;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getSynchronize() {
		return synchronize;
	}
	public void setSynchronize(int synchronize) {
		this.synchronize = synchronize;
	}
	
	
	

}
