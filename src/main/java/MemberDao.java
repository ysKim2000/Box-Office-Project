public class MemberDao {
    // Data Access Object: DB를 사용하여 데이터를 조회하거나 조작하는 기능을 모아둔 Object(클래스)
    DB db;
    Member[] members;
    int lastMemberIndex;
    int lastMemberId;

    MemberDao(DB db) {
        this.db = db;

        members = db.getMembersFromFiles();

        for (int i = 0; i < members.length; i++) {
            if (members[i] == null) {
                lastMemberIndex = i - 1;
                break;
            }
        }
        lastMemberId = db.getLastMemberId();
    }

    boolean isUsedloginId(String loginId) {
        Member member = db.getMemberByLoginId(loginId);

        return member != null;
    }
    boolean isUsedloginPw(String loginId, String loginPw) {
        Member member = db.getMemberByLoginPw(loginId, loginPw);
        return member != null;
    }

    void add(String loginId, String loginPw, String loginBirth, String regName) {
        int id = lastMemberId + 1;
        String regDate = Util.getNowDateStr();
        Member member = new Member(id, regDate, loginId, loginPw, loginBirth, regName);
        lastMemberIndex++;
        members[lastMemberIndex] = member;

        String memberFilePath = "member/" + member.id + ".txt";
        db.writeMemberJsonFile(memberFilePath, member);

        lastMemberId++;
        db.updateLastArticleId(lastMemberId);
    }
}
