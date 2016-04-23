package com.simpletour.company.web.query.sale;


import com.simpletour.commons.data.dao.query.ConditionOrderByQuery;
import com.simpletour.commons.data.dao.query.condition.AndConditionSet;
import com.simpletour.commons.data.dao.query.condition.Condition;
import com.simpletour.company.web.query.support.Query;

import java.util.Date;

/**
 * @Brief :  ${用途}
 * @Author: liangfei/liangfei@simpletour.com
 * @Date :  2016/4/22 15:06
 * @Since ： ${VERSION}
 * @Remark: ${Remark}
 */
public class AgreementProductPriceQuery extends Query {

    private Long agreementProductId;

    private Date startDate;

    private Date endDate;




    public AgreementProductPriceQuery() {
    }

    public AgreementProductPriceQuery(Long agreementProductId, Date startDate, Date endDate) {
        this.agreementProductId = agreementProductId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AgreementProductPriceQuery(int index, int size, Long agreementProductId, Date startDate, Date endDate) {
        super(index, size);
        this.agreementProductId = agreementProductId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AgreementProductPriceQuery(int index, int size, Date startDate, Date endDate) {
        super(index, size);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getAgreementProductId() {
        return agreementProductId;
    }

    public void setAgreementProductId(Long agreementProductId) {
        this.agreementProductId = agreementProductId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}