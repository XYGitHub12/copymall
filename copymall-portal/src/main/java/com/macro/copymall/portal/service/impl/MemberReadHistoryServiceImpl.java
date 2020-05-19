package com.macro.copymall.portal.service.impl;

import com.macro.copymall.portal.domain.MemberReadHistory;
import com.macro.copymall.portal.repository.MemberReadHistoryRepository;
import com.macro.copymall.portal.service.MemberReadHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 会员浏览记录管理Service实现类
 */
@Service
public class MemberReadHistoryServiceImpl implements MemberReadHistoryService {

    @Autowired
    private MemberReadHistoryRepository memberReadHistoryRepository;

    /**
     * 生成浏览记录
     * @param memberReadHistory
     * @return
     */
    @Override
    public int create(MemberReadHistory memberReadHistory) {
        memberReadHistory.setId(null);
        memberReadHistory.setCreateTime(new Date());
        memberReadHistoryRepository.save(memberReadHistory);
        return 1;
    }

    /**
     * 批量删除浏览记录
     * @param ids
     * @return
     */
    @Override
    public int delete(List<String> ids) {
        List<MemberReadHistory> deleteList = new ArrayList<>();
        for(String id:ids){
            MemberReadHistory memberReadHistory = new MemberReadHistory();
            memberReadHistory.setId(id);
            deleteList.add(memberReadHistory);
        }
        memberReadHistoryRepository.deleteAll(deleteList);
        return ids.size();
    }

    /**
     * 获取用户浏览历史记录
     * @param memberId
     * @return
     */
    @Override
    public List<MemberReadHistory> list(Long memberId) {
        return memberReadHistoryRepository.findByMemberIdOrderByCreateTimeDesc(memberId);
    }
}
