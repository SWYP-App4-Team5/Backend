package com.jjanpot.server.global.service.push;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.jjanpot.server.domain.notification.dto.FcmSendCommand;

public interface PushSendService {
	CompletableFuture<List<FcmSendResult>> sendMessage(List<FcmSendCommand> fcmSendCommands);
}
