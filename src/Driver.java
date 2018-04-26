public class Driver
{

    private String id, name, surname, url;


    public String getDriverId()
    {
        return id;
    }

    public String getDriverName()
    {
        return name;
    }

    public String getDriverSurname()
    {
        return surname;
    }

    public String getDriverUrl()
    {
        return url;
    }


    public Driver(String id, String givenName, String familyName, String url)
    {
        this.id = id;
        this.name = givenName;
        this.surname = familyName;
        this.url = url;
    }

}
