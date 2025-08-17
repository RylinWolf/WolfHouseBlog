package com.wolfhouse.wolfhouseblog.auth.service.verify.impl.nodes.article;

import com.wolfhouse.wolfhouseblog.auth.service.verify.impl.BaseVerifyNode;
import com.wolfhouse.wolfhouseblog.common.enums.VisibilityEnum;
import com.wolfhouse.wolfhouseblog.common.exceptions.ServiceException;
import com.wolfhouse.wolfhouseblog.common.utils.ServiceUtil;
import com.wolfhouse.wolfhouseblog.pojo.domain.Article;
import com.wolfhouse.wolfhouseblog.pojo.domain.table.ArticleTableDef;
import com.wolfhouse.wolfhouseblog.pojo.dto.ArticleQueryPageDto;
import com.wolfhouse.wolfhouseblog.service.ArticleService;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

/**
 * @author linexsong
 */
public class IdReachableVerifyNode extends BaseVerifyNode<Long> {
    private final ArticleService service;

    public IdReachableVerifyNode(ArticleService service) {
        super();
        this.service = service;
        this.customException = ServiceException.notAllowed();
    }

    @Override
    public IdReachableVerifyNode target(Long target) {
        this.t = target;
        return this;
    }

    @Override
    public boolean verify() {
        Long login = ServiceUtil.loginUser();
        var dto = new ArticleQueryPageDto();
        dto.setId(JsonNullable.of(t));

        List<Article> records = null;
        try {
            records = service.queryBy(
                                  dto,
                                  ArticleTableDef.ARTICLE.ID,
                                  ArticleTableDef.ARTICLE.VISIBILITY,
                                  ArticleTableDef.ARTICLE.AUTHOR_ID)
                             .getRecords();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }

        // 无该 ID 记录
        if (records.isEmpty()) {
            return false;
        }

        Article art = records.getFirst();
        // 公开权限
        if (art.getVisibility()
               .equals(VisibilityEnum.PUBLIC)) {
            return true;
        }

        // 登录用户为作者
        return super.verify() && art.getAuthorId()
                                    .equals(login);
    }
}
