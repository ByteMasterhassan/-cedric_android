package com.cedricapp.utils;

import android.content.res.Resources;

import com.cedricapp.R;

public class ResponseStatus {

    public static String getResponseCodeMessage(int responseCode, Resources resources) {
        String message = "";
        switch (responseCode) {
            case 100:
                message = resources.getString(R.string._continue);
                break;
            case 101:
                message = resources.getString(R.string.switching_protocols);
                break;
            case 102:
                message = resources.getString(R.string.processing);
                break;
            case 200:
                message = resources.getString(R.string.ok);
                break;
            case 201:
                message =resources.getString(R.string.created);
                break;
            case 202:
                message = resources.getString(R.string.accepted);
                break;
            case 203:
                message = resources.getString(R.string.non_authoritative_information);
                break;
            case 204:
                message = resources.getString(R.string.no_content);
                break;
            case 205:

                message = resources.getString(R.string.reset_content);
                break;
            case 206:
                message = resources.getString(R.string.partial_content);
                break;
            case 207:
                message = resources.getString(R.string.multi_status);
                break;
            case 300:
                message = resources.getString(R.string.multiple_choices);
                break;
            case 301:
                message = resources.getString(R.string.moved_permanently);
                break;
            case 302:
                message = resources.getString(R.string.moved_temporarily);
                break;
            case 303:
                message = resources.getString(R.string.see_other);
                break;
            case 304:
                message = resources.getString(R.string.not_modified);
                break;
            case 305:
                message =resources.getString(R.string.use_proxy);
                break;
            case 307:
                message = resources.getString(R.string.temporary_redirect);
                break;
            case 308:
                message = resources.getString(R.string.permanent_redirect);
                break;
            case 400:
                message = resources.getString(R.string.bad_request);
                break;
            case 401:
                message = resources.getString(R.string.unauthorized);
                break;
            case 402:
                message = resources.getString(R.string.payment_required);
                break;
            case 403:
                message = resources.getString(R.string.forbidden);
                break;
            case 404:
                message = resources.getString(R.string.not_found);
                break;
            case 405:
                message = resources.getString(R.string.method_not_allowed);
                break;
            case 406:
                message = resources.getString(R.string.not_acceptable);
                break;
            case 407:
                message = resources.getString(R.string.proxy_authentication_required);
                break;
            case 408:
                message = resources.getString(R.string.request_timeout);
                break;
            case 409:
                message = resources.getString(R.string.conflict);
                break;
            case 410:
                message = resources.getString(R.string.gone);
                break;
            case 411:
                message = resources.getString(R.string.length_required);
                break;
            case 412:
                message = resources.getString(R.string.precondition_failed);
                break;
            case 413:
                message = resources.getString(R.string.request_entity_too_large);
                break;
            case 414:
                message = resources.getString(R.string.request_uri_too_long);
                break;
            case 415:
                message = resources.getString(R.string.unsupported_media_type);
                break;
            case 416:
                message = resources.getString(R.string.requested_range_not_satisfiable);
                break;
            case 417:
                message = resources.getString(R.string.expectation_failed);
                break;
            case 418:
                message = resources.getString(R.string.i_m_a_teapot);
                break;
            case 419:
                message = resources.getString(R.string.insufficient_space_on_resource);
                break;
            case 420:
                message = resources.getString(R.string.method_failure);
                break;
            case 421:
                message = resources.getString(R.string.misdirected_request);
                break;
            case 422:
                message = resources.getString(R.string.unprocessable_entity);
                break;
            case 423:
                message = resources.getString(R.string.locked);
                break;
            case 424:
                message = resources.getString(R.string.failed_dependency);
                break;
            case 428:
                message = resources.getString(R.string.precondition_required);
                break;
            case 429:
                message = resources.getString(R.string.too_many_requests);
                break;
            case 431:
                message = resources.getString(R.string.request_header_fields_too_large);
                break;
            case 451:
                message = resources.getString(R.string.unavailable_for_legal_reasons);
                break;
            case 500:
                message = resources.getString(R.string.internal_server_error);
                break;
            case 501:
                message = resources.getString(R.string.not_implemented);
                break;
            case 502:
                message = resources.getString(R.string.bad_gateway);
                break;
            case 503:
                message = resources.getString(R.string.service_unavailable);
                break;
            case 504:
                message = resources.getString(R.string.gateway_timeout);
                break;
            case 505:
                message = resources.getString(R.string.http_version_not_supported);
                break;
            case 507:
                message = resources.getString(R.string.insufficient_storage);
                break;
            case 511:
                message = resources.getString(R.string.network_authentication_required);
                break;

            default:
                message = resources.getString(R.string.please_wait_we_are_working_on_it);
        }

        return message;
    }

}
