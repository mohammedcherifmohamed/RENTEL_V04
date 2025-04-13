package controllers ;

public class RentalReminder {
    private int rentalId;
    private String clientName;
    private String vehicleBrand;
    private String vehicleModel;
    private String endDate;

    public RentalReminder(int rentalId, String clientName , String endDate, String vehicleBrand, String vehicleModel) {
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
        this.rentalId = rentalId;
        this.clientName = clientName;
        this.endDate = endDate;
    }

    public String getClientName() {
        return clientName;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }
    public String getVehicleModel() {
        return vehicleModel;
    }

    public String getEndDate() {
        return endDate;
    }
}
