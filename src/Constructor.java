public class Constructor
{
    private String constructorId;
    private String constructorName;
    private String constructorUrl;

    public String getConstructorId()
    {
        return constructorId;
    }


    public String getConstructorName()
    {
        return constructorName;
    }
    public String getConstructorUrl()
    {
        return constructorUrl;
    }




    public Constructor(String name, String id, String url)
    {
        this.constructorName = name;
        this.constructorId = id;
        this.constructorUrl = url;
    }

}
