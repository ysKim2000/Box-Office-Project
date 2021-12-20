public class MemberService {
    MemberDao memberDao;

    MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    boolean join(String loginId, String loginPw, String loginBirth, String regName) {
        if (isUsedLoginId(loginId)) {
            return false;
        }

        memberDao.add(loginId, loginPw, loginBirth, regName);
        return true;
    }

    boolean isUsedLoginId(String loginId) {
        return memberDao.isUsedloginId(loginId);
    }

    boolean isUsedLoginPw(String loginId, String loginPw) {
        return memberDao.isUsedloginPw(loginId, loginPw);
    }
}
