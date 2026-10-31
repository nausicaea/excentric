package net.nausicaea.excentric.ai

interface Needful {
    fun villageModNeeds(): NeedsCollection

    fun villageModReasoner(): NeedsReasoner
}
