package com.mischiefsmp.enchanttiers;

public record TierPlacementResult(boolean success, boolean isOnTierBlock, String blockId, String blockPrettyPrint, int tier) { }
