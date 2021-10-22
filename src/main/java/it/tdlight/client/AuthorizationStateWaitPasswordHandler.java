package it.tdlight.client;

import it.tdlight.common.ExceptionHandler;
import it.tdlight.common.TelegramClient;
import it.tdlight.jni.TdApi.AuthorizationStateWaitPassword;
import it.tdlight.jni.TdApi.CheckAuthenticationPassword;
import it.tdlight.jni.TdApi.UpdateAuthorizationState;

final class AuthorizationStateWaitPasswordHandler implements GenericUpdateHandler<UpdateAuthorizationState> {

	private final TelegramClient client;
	private final ClientInteraction clientInteraction;
	private final ExceptionHandler exceptionHandler;

	public AuthorizationStateWaitPasswordHandler(TelegramClient client,
			ClientInteraction clientInteraction,
			ExceptionHandler exceptionHandler) {
		this.client = client;
		this.clientInteraction = clientInteraction;
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public void onUpdate(UpdateAuthorizationState update) {
		if (update.authorizationState.getConstructor() == AuthorizationStateWaitPassword.CONSTRUCTOR) {
			AuthorizationStateWaitPassword authorizationState = (AuthorizationStateWaitPassword) update.authorizationState;
			ParameterInfo parameterInfo = new ParameterInfoPasswordHint(authorizationState.passwordHint,
					authorizationState.hasRecoveryEmailAddress,
					authorizationState.recoveryEmailAddressPattern
			);
			String password = clientInteraction.onParameterRequest(InputParameter.ASK_PASSWORD, parameterInfo);
			CheckAuthenticationPassword response = new CheckAuthenticationPassword(password);
			client.send(response, ok -> {
				if (ok.getConstructor() == Error.CONSTRUCTOR) {
					throw new TelegramError((Error) ok);
				}
			}, exceptionHandler);
		}
	}
}