/*
 * Copyright 2014-2019 Groupon, Inc
 * Copyright 2014-2019 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.usage.api;

import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.killbill.billing.osgi.api.OSGIServiceRegistration;
import org.killbill.billing.usage.plugin.api.RawUsageRecord;
import org.killbill.billing.usage.plugin.api.UsagePluginApi;
import org.killbill.billing.util.callcontext.TenantContext;

public class BaseUserApi {

    private final OSGIServiceRegistration<UsagePluginApi> pluginRegistry;

    public BaseUserApi(final OSGIServiceRegistration<UsagePluginApi> pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    protected List<RawUsageRecord> getUsageFromPlugin(final LocalDate startDate, final LocalDate endDate, final TenantContext tenantContext) {

        final Set<String> allServices = pluginRegistry.getAllServices();
        // No plugin registered
        if (allServices.isEmpty()) {
            return null;
        }

        for (final String service : allServices) {
            final UsagePluginApi plugin = pluginRegistry.getServiceForName(service);

            final List<RawUsageRecord> result = plugin.geUsageForAccount(startDate, endDate, tenantContext);
            // First plugin registered, returns result -- could be empty List if no usage was recorded.
            if (result != null) {
                return result;
            }
        }
        // All registered plugins returned null
        return null;
    }

}
