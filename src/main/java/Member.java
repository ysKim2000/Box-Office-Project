public class Member {
    public int id;
    public String regDate;
    public String loginId;
    public String loginPw;
    public String loginBirth;
    public String regName;

    public Member() {

    }
    public Member(int id, String regDate, String loginId, String loginPw, String loginBirth, String regName) {
        super();
        this.id = id;
        this.regDate = regDate;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.regName = regName;
        this.loginBirth = loginBirth;
    }

    @Override
    public String toString() {
        return "Member [id=" + id + ", regDate=" + regDate + ", loginId=" + loginId + ", loginPw=" + loginPw + ", Name= " + regName +  ", Birth=" + loginBirth + "]";
    }
}
