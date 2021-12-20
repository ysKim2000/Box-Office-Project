import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DB {
    Global global;

    public Member getMemberByLoginId(String loginId) {
        List<Member> members = getMembers();

        for (Member member : members) {
            if (member.loginId.equals(loginId)) {
                return member;
            }
        }
        return null;
    }
    public Member getMemberByLoginPw(String loginId, String loginPw) {
        List<Member> members = getMembers();

        for (Member member : members) {
            if (member.loginId.equals(loginId)) {
                if (member.loginPw.equals(loginPw)){
                    return member;
                }
            }
        }
        return null;
    }

    List<Member> getMembers() {
        List<Member> members = new ArrayList<>();
        int lastId = getLastMemberId();

        for (int i = 1; i <= lastId; i++) {
            String filePath = "member/" + i + ".txt";
            Member member = readMemberFromJsonFile(filePath);
            if (member != null) {
                members.add(member);
            }
        }
        return members;
    }

    void updateLastArticleId(int lastMemberId) {
        global.lastMemberId = lastMemberId;

        writeGlobalJsonFile(global);

    }

    Member[] getMembersFromFiles() {
        Member[] members = new Member[100];

        int lastMemberId = getLastMemberId();

        int membersIndex = 0;
        for (int i = 1; i <= lastMemberId; i++) {
            String fileName = "member/" + i + ".txt";
            Member member = readMemberFromJsonFile(fileName);

            if (member != null) {
                members[membersIndex] = member;
                membersIndex++;
            }
        }
        return members;
    }

    void writeGlobalJsonFile(Global global) {
        ObjectMapper om = new ObjectMapper();

        try {
            om.writeValue(new File("member/global.txt"), global);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadGlobal() {
        global = readGlobalFromJsonFile();
    }

    int getLastMemberId() {
        return global.lastMemberId;
    }

    Member readMemberFromJsonFile(String filePath) {
        ObjectMapper om = new ObjectMapper();
        Member member = null;

        try {
            member = om.readValue(new File(filePath), Member.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return member;
    }

    Global readGlobalFromJsonFile() {
        ObjectMapper om = new ObjectMapper();
        Global global = null;

        try {
            global = om.readValue(new File("member/global.txt"), Global.class);
        } catch (JsonParseException | JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // 파일 없으면 여기서 생성
            global = new Global();
            writeGlobalJsonFile(global);
        }

        return global;
    }

    void writeMemberJsonFile(String filePath, Member member) {
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(new File(filePath), member);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
