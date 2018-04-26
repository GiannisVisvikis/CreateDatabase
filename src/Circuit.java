public class Circuit
{
    private String circuitId;
    private String circuitName;
    private String circuitUrl;


    public String getCircuitId()
    {
        return circuitId;
    }


    public String getCircuitName()
    {
        return circuitName;
    }

    public String getCircuitUrl(){return circuitUrl;}


    public Circuit(String name, String id, String url)
    {
        this.circuitName = name;
        this.circuitId = id;
        this.circuitUrl = url;
    }


}
