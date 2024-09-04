package com.jxx.groupware.api.work.listener;

import com.jxx.groupware.core.ConfirmCreateForm;

/** REST API **/
public record RestApiRequestEvent(

        ConfirmCreateForm confirmCreateForm,
        String method,
        String baseUrl,
        String path

) {
}
