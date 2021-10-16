package dev.latvian.kubejs.integration.gamestages;

public class GameStagesWrapper {/* FIXME: Gamestages extends Stages {
	public GameStagesWrapper(Player p) {
		super(p);
	}

	@Override
	public boolean addNoUpdate(String stage) {
		IStageData stageData = GameStageHelper.getPlayerData(player);

		if (stageData != null && !stageData.hasStage(stage)) {
			stageData.addStage(stage);
			return true;
		}

		return false;
	}

	@Override
	public boolean removeNoUpdate(String stage) {
		IStageData stageData = GameStageHelper.getPlayerData(player);

		if (stageData != null && stageData.hasStage(stage)) {
			stageData.removeStage(stage);
			return true;
		}

		return false;
	}

	@Override
	public Collection<String> getAll() {
		IStageData stageData = GameStageHelper.getPlayerData(player);

		return stageData != null ? stageData.getStages() : Collections.emptyList();
	}

	@Override
	public void replace(Collection<String> stages) {
		IStageData stageData = new StageData();

		for (String s : stages) {
			stageData.addStage(s);
		}

		setClientData(stageData);
	}

	private void setClientData(IStageData stageData) {
		GameStageClientHelper.setClientData(stageData);
	}
	*/
}