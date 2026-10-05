package org.murlan.live.endpoint.session;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

public record DisconnectionTask(UUID roomId, ScheduledFuture<?> graceTask) {}
