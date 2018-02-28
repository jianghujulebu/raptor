package com.ppdai.framework.raptor.filter.provider;

import com.ppdai.framework.raptor.common.RaptorConstants;
import com.ppdai.framework.raptor.common.URLParamType;
import com.ppdai.framework.raptor.filter.AbstractAccessLogFilter;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProviderAccessLogFilter extends AbstractAccessLogFilter implements ProviderFilter {

    @Override
    public int getOrder() {
        return 1000;
    }

    @Override
    public Response filter(Provider<?> provider, Request request) {
        URL serviceUrl = provider.getServiceUrl();
        boolean needLog = serviceUrl.getBooleanParameter(URLParamType.accessLog.getName(), URLParamType.accessLog.getBooleanValue());
        if (needLog) {
            long t = System.currentTimeMillis();
            Response response = null;

            try {
                response = provider.call(request);
                return response;
            } finally {
                long requestTime = System.currentTimeMillis() - t;
                logAccess(provider.getServiceUrl(), request, response, requestTime);
            }
        }
        return provider.call(request);
    }

    @Override
    protected String getNodeType() {
        return RaptorConstants.NODE_TYPE_SERVICE;
    }
}
