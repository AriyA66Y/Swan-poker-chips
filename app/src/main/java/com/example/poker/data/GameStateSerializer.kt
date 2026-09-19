package com.example.poker.data

import com.example.poker.model.ActionType
import com.example.poker.model.BuyInRecord
import com.example.poker.model.ChipAdjustment
import com.example.poker.model.GameSettings
import com.example.poker.model.HandResult
import com.example.poker.model.Player
import com.example.poker.model.PlayerAction
import com.example.poker.model.PlayerHandStatus
import com.example.poker.model.Pot
import com.example.poker.model.Street
import com.example.poker.viewmodel.PokerUiState
import org.json.JSONArray
import org.json.JSONObject

object GameStateSerializer {

    fun serialize(state: PokerUiState): String {
        val root = JSONObject()

        // Players
        val playersArray = JSONArray()
        for (p in state.players) {
            val pObj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("seatIndex", p.seatIndex)
                put("chips", p.chips)
                put("totalBuyIn", p.totalBuyIn)
                put("currentStreetBet", p.currentStreetBet)
                put("totalHandContributed", p.totalHandContributed)
                put("status", p.status.name)
                put("colorHex", p.colorHex)
                put("hasActedThisRound", p.hasActedThisRound)
            }
            playersArray.put(pObj)
        }
        root.put("players", playersArray)

        // Settings
        val sObj = JSONObject().apply {
            put("smallBlind", state.settings.smallBlind)
            put("bigBlind", state.settings.bigBlind)
            put("ante", state.settings.ante)
            put("autoPostBlinds", state.settings.autoPostBlinds)
            put("currencyName", state.settings.currencyName)
        }
        root.put("settings", sObj)

        // Game Progress
        root.put("dealerIndex", state.dealerIndex)
        root.put("handNumber", state.handNumber)
        root.put("currentStreet", state.currentStreet.name)
        root.put("currentTurnPlayerId", state.currentTurnPlayerId ?: JSONObject.NULL)
        root.put("currentHighestBet", state.currentHighestBet)
        root.put("minRaise", state.minRaise)
        root.put("lastHandSummary", state.lastHandSummary ?: JSONObject.NULL)

        // Pots
        val potsArray = JSONArray()
        for (pot in state.pots) {
            val potObj = JSONObject().apply {
                put("id", pot.id)
                put("name", pot.name)
                put("amount", pot.amount)
                val eligArray = JSONArray()
                pot.eligiblePlayerIds.forEach { eligArray.put(it) }
                put("eligiblePlayerIds", eligArray)
                val winArray = JSONArray()
                pot.winners.forEach { winArray.put(it) }
                put("winners", winArray)
            }
            potsArray.put(potObj)
        }
        root.put("pots", potsArray)

        // Hand History
        val historyArray = JSONArray()
        for (hh in state.handHistory) {
            val hhObj = JSONObject().apply {
                put("id", hh.id)
                put("handNumber", hh.handNumber)
                put("timestamp", hh.timestamp)
                put("totalPot", hh.totalPot)
                put("winnersSummary", hh.winnersSummary)

                val hhPotsArray = JSONArray()
                for (pot in hh.pots) {
                    val hpObj = JSONObject().apply {
                        put("id", pot.id)
                        put("name", pot.name)
                        put("amount", pot.amount)
                        val elig = JSONArray()
                        pot.eligiblePlayerIds.forEach { elig.put(it) }
                        put("eligiblePlayerIds", elig)
                        val win = JSONArray()
                        pot.winners.forEach { win.put(it) }
                        put("winners", win)
                    }
                    hhPotsArray.put(hpObj)
                }
                put("pots", hhPotsArray)

                val deltasObj = JSONObject()
                for ((pid, delta) in hh.playerChipDeltas) {
                    deltasObj.put(pid, delta)
                }
                put("playerChipDeltas", deltasObj)

                val actionsArray = JSONArray()
                for (act in hh.actionLogs) {
                    val actObj = JSONObject().apply {
                        put("id", act.id)
                        put("playerId", act.playerId)
                        put("playerName", act.playerName)
                        put("actionType", act.actionType.name)
                        put("amount", act.amount)
                        put("street", act.street.name)
                        put("timestamp", act.timestamp)
                    }
                    actionsArray.put(actObj)
                }
                put("actionLogs", actionsArray)
            }
            historyArray.put(hhObj)
        }
        root.put("handHistory", historyArray)

        // BuyIn Records
        val buyInArray = JSONArray()
        for (b in state.buyInRecords) {
            val bObj = JSONObject().apply {
                put("id", b.id)
                put("playerId", b.playerId)
                put("playerName", b.playerName)
                put("amount", b.amount)
                put("timestamp", b.timestamp)
                put("isInitial", b.isInitial)
            }
            buyInArray.put(bObj)
        }
        root.put("buyInRecords", buyInArray)

        // Chip Adjustments
        val adjArray = JSONArray()
        for (a in state.chipAdjustments) {
            val aObj = JSONObject().apply {
                put("id", a.id)
                put("playerId", a.playerId)
                put("playerName", a.playerName)
                put("delta", a.delta)
                put("reason", a.reason)
                put("timestamp", a.timestamp)
            }
            adjArray.put(aObj)
        }
        root.put("chipAdjustments", adjArray)

        return root.toString()
    }

    fun deserialize(jsonStr: String): PokerUiState? {
        return try {
            val root = JSONObject(jsonStr)

            // Players
            val playersList = mutableListOf<Player>()
            val playersArray = root.optJSONArray("players") ?: JSONArray()
            for (i in 0 until playersArray.length()) {
                val pObj = playersArray.getJSONObject(i)
                val statusStr = pObj.optString("status", PlayerHandStatus.ACTIVE.name)
                val status = try {
                    PlayerHandStatus.valueOf(statusStr)
                } catch (e: Exception) {
                    PlayerHandStatus.ACTIVE
                }

                playersList.add(
                    Player(
                        id = pObj.getString("id"),
                        name = pObj.getString("name"),
                        seatIndex = pObj.optInt("seatIndex", i),
                        chips = pObj.optLong("chips", 0L),
                        totalBuyIn = pObj.optLong("totalBuyIn", 0L),
                        currentStreetBet = pObj.optLong("currentStreetBet", 0L),
                        totalHandContributed = pObj.optLong("totalHandContributed", 0L),
                        status = status,
                        colorHex = pObj.optLong("colorHex", 0xFF1E88E5L),
                        hasActedThisRound = pObj.optBoolean("hasActedThisRound", false)
                    )
                )
            }

            // Settings
            val sObj = root.optJSONObject("settings")
            val settings = if (sObj != null) {
                GameSettings(
                    smallBlind = sObj.optLong("smallBlind", 10L),
                    bigBlind = sObj.optLong("bigBlind", 20L),
                    ante = sObj.optLong("ante", 0L),
                    autoPostBlinds = sObj.optBoolean("autoPostBlinds", true),
                    currencyName = sObj.optString("currencyName", "چیپ")
                )
            } else {
                GameSettings()
            }

            val dealerIndex = root.optInt("dealerIndex", 0)
            val handNumber = root.optInt("handNumber", 0)
            val streetStr = root.optString("currentStreet", Street.ENDED.name)
            val currentStreet = try {
                Street.valueOf(streetStr)
            } catch (e: Exception) {
                Street.ENDED
            }

            val currentTurnPlayerId = if (root.isNull("currentTurnPlayerId")) null else root.optString("currentTurnPlayerId")
            val currentHighestBet = root.optLong("currentHighestBet", 0L)
            val minRaise = root.optLong("minRaise", settings.bigBlind)
            val lastHandSummary = if (root.isNull("lastHandSummary")) null else root.optString("lastHandSummary")

            // Pots
            val potsList = mutableListOf<Pot>()
            val potsArray = root.optJSONArray("pots") ?: JSONArray()
            for (i in 0 until potsArray.length()) {
                val potObj = potsArray.getJSONObject(i)
                val eligArray = potObj.optJSONArray("eligiblePlayerIds") ?: JSONArray()
                val elig = (0 until eligArray.length()).map { eligArray.getString(it) }
                val winArray = potObj.optJSONArray("winners") ?: JSONArray()
                val winners = (0 until winArray.length()).map { winArray.getString(it) }

                potsList.add(
                    Pot(
                        id = potObj.optString("id", "pot_$i"),
                        name = potObj.optString("name", "پات"),
                        amount = potObj.optLong("amount", 0L),
                        eligiblePlayerIds = elig,
                        winners = winners
                    )
                )
            }

            // Hand History
            val historyList = mutableListOf<HandResult>()
            val historyArray = root.optJSONArray("handHistory") ?: JSONArray()
            for (i in 0 until historyArray.length()) {
                val hhObj = historyArray.getJSONObject(i)
                val hhPotsArray = hhObj.optJSONArray("pots") ?: JSONArray()
                val hhPots = mutableListOf<Pot>()
                for (j in 0 until hhPotsArray.length()) {
                    val hpObj = hhPotsArray.getJSONObject(j)
                    val eligArray = hpObj.optJSONArray("eligiblePlayerIds") ?: JSONArray()
                    val elig = (0 until eligArray.length()).map { eligArray.getString(it) }
                    val winArray = hpObj.optJSONArray("winners") ?: JSONArray()
                    val winners = (0 until winArray.length()).map { winArray.getString(it) }
                    hhPots.add(
                        Pot(
                            id = hpObj.optString("id", "pot_$j"),
                            name = hpObj.optString("name", "پات"),
                            amount = hpObj.optLong("amount", 0L),
                            eligiblePlayerIds = elig,
                            winners = winners
                        )
                    )
                }

                val deltasMap = mutableMapOf<String, Long>()
                val deltasObj = hhObj.optJSONObject("playerChipDeltas")
                if (deltasObj != null) {
                    val keys = deltasObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        deltasMap[key] = deltasObj.optLong(key, 0L)
                    }
                }

                val actArray = hhObj.optJSONArray("actionLogs") ?: JSONArray()
                val actList = mutableListOf<PlayerAction>()
                for (k in 0 until actArray.length()) {
                    val aObj = actArray.getJSONObject(k)
                    val aType = try {
                        ActionType.valueOf(aObj.optString("actionType"))
                    } catch (e: Exception) {
                        ActionType.CHECK
                    }
                    val aStreet = try {
                        Street.valueOf(aObj.optString("street"))
                    } catch (e: Exception) {
                        Street.PREFLOP
                    }
                    actList.add(
                        PlayerAction(
                            id = aObj.optString("id"),
                            playerId = aObj.optString("playerId"),
                            playerName = aObj.optString("playerName"),
                            actionType = aType,
                            amount = aObj.optLong("amount", 0L),
                            street = aStreet,
                            timestamp = aObj.optLong("timestamp", 0L)
                        )
                    )
                }

                historyList.add(
                    HandResult(
                        id = hhObj.optString("id"),
                        handNumber = hhObj.optInt("handNumber", i + 1),
                        timestamp = hhObj.optLong("timestamp", 0L),
                        totalPot = hhObj.optLong("totalPot", 0L),
                        pots = hhPots,
                        playerChipDeltas = deltasMap,
                        winnersSummary = hhObj.optString("winnersSummary", ""),
                        actionLogs = actList
                    )
                )
            }

            // Buy In Records
            val buyInList = mutableListOf<BuyInRecord>()
            val buyInArray = root.optJSONArray("buyInRecords") ?: JSONArray()
            for (i in 0 until buyInArray.length()) {
                val bObj = buyInArray.getJSONObject(i)
                buyInList.add(
                    BuyInRecord(
                        id = bObj.optString("id"),
                        playerId = bObj.optString("playerId"),
                        playerName = bObj.optString("playerName"),
                        amount = bObj.optLong("amount", 0L),
                        timestamp = bObj.optLong("timestamp", 0L),
                        isInitial = bObj.optBoolean("isInitial", false)
                    )
                )
            }

            // Chip Adjustments
            val adjList = mutableListOf<ChipAdjustment>()
            val adjArray = root.optJSONArray("chipAdjustments") ?: JSONArray()
            for (i in 0 until adjArray.length()) {
                val aObj = adjArray.getJSONObject(i)
                adjList.add(
                    ChipAdjustment(
                        id = aObj.optString("id"),
                        playerId = aObj.optString("playerId"),
                        playerName = aObj.optString("playerName"),
                        delta = aObj.optLong("delta", 0L),
                        reason = aObj.optString("reason", ""),
                        timestamp = aObj.optLong("timestamp", 0L)
                    )
                )
            }

            PokerUiState(
                players = playersList,
                dealerIndex = dealerIndex,
                currentStreet = currentStreet,
                currentTurnPlayerId = currentTurnPlayerId,
                currentHighestBet = currentHighestBet,
                minRaise = minRaise,
                pots = potsList,
                handNumber = handNumber,
                actionLogs = emptyList(),
                handHistory = historyList,
                buyInRecords = buyInList,
                chipAdjustments = adjList,
                settings = settings,
                isShowdownModalOpen = false,
                isAutoHandOver = false,
                lastHandSummary = lastHandSummary
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
