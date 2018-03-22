public class Identifier {

    private String userid;
    private String name;
    private String github;

    public Identifier(String aName, String aGithub) {
        this.userid = " ";
        this.name = aName;
        this.github = aGithub;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGithub() {
        return github;
    }

    public void setGithubid(String github) {
        this.github = github;
    }
}
