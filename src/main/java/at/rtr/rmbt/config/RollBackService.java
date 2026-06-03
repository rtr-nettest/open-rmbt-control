package at.rtr.rmbt.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Roll back service class.
 */
@Component
public class RollBackService {

    public void setRollBackOnly() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }

}
