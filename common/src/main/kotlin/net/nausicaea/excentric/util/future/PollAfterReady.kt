package net.nausicaea.excentric.util.future

class PollAfterReady : IllegalStateException("cannot poll a future after is has entered ready state")
