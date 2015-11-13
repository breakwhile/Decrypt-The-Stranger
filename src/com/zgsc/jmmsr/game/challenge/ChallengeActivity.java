package com.zgsc.jmmsr.game.challenge;

import com.zgsc.jmmsr.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChallengeActivity extends Activity {
	private Intent intent;
	private int challengeScore;
	private boolean isAcceptChallenge = false;

	public ChallengeActivity() {
		mainActivity = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		String mode = intent.getStringExtra("mode");
		if (mode.equals("acceptChallenge")) {
			isAcceptChallenge = true;
			challengeScore = intent.getIntExtra("score", 0);
			String tempScore = intent.getStringExtra("score");
			try {
				challengeScore = Integer.parseInt(tempScore);
			} catch (Exception e) {
				challengeScore = 100;
			}
		} else if (mode.equals("toChallenge")) {
			challengeScore = 0;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game_challenge);

		root = (LinearLayout) findViewById(R.id.container);
		root.setBackgroundColor(0xfffaf8ef);

		tvScore = (TextView) findViewById(R.id.tvScore);
		tvBestScore = (TextView) findViewById(R.id.tvBestScore);

		gameView = (GameView) findViewById(R.id.gameView);

		btnNewGame = (Button) findViewById(R.id.btnNewGame);
		btnNewGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// gameView.startGame();
				//提交分数
				intent.putExtra("challengeResult", "finish");
				intent.putExtra("score", String.valueOf(score));
				if (isAcceptChallenge) {
					ChallengeActivity.this.setResult(122, intent);
				} else {
					ChallengeActivity.this.setResult(121, intent);
				}
				finish();
			}
		});

		animLayer = (AnimLayer) findViewById(R.id.animLayer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_challenge, menu);
		return true;
	}

	public void clearScore() {
		score = 0;
		showScore();
	}

	public void showScore() {
		tvScore.setText(score + "");
	}

	public void addScore(int s) {
		score += s;
		showScore();
		/*
		 * int maxScore = Math.max(score, getBestScore());
		 * saveBestScore(maxScore);
		 */
		if (isAcceptChallenge) {
			if (score > getBestScore()) {
				new AlertDialog.Builder(this).setTitle("挑战结束！")
						.setMessage("成功挑战，是否加为好友？")
						.setPositiveButton("加为好友", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Toast.makeText(ChallengeActivity.this,
								// "加为好友！", Toast.LENGTH_SHORT).show();
								intent.putExtra("challengeResult", "addFriend");
								intent.putExtra("userId",
										intent.getStringExtra("userId"));
								ChallengeActivity.this.setResult(122, intent);
								ChallengeActivity.this.finish();
							}
						}).setNegativeButton("退出", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Toast.makeText(ChallengeActivity.this, "退出！",
								// Toast.LENGTH_SHORT).show();
								intent.putExtra("challengeResult", "exit");
								ChallengeActivity.this.setResult(122, intent);
								ChallengeActivity.this.finish();
							}
						}).show();
				showBestScore(score);
			} else {
				showBestScore(getBestScore());
			}
		}
	}

	public void saveBestScore(int s) {
		Editor e = getPreferences(MODE_PRIVATE).edit();
		e.putInt(SP_KEY_BEST_SCORE, s);
		e.commit();
	}

	public int getBestScore() {
		// return getPreferences(MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
		return challengeScore;
	}

	public void showBestScore(int s) {
		if (isAcceptChallenge) {
			tvBestScore.setText(s + "");
		}
	}

	public AnimLayer getAnimLayer() {
		return animLayer;
	}

	private int score = 0;
	private TextView tvScore, tvBestScore;
	private LinearLayout root = null;
	private Button btnNewGame;
	private GameView gameView;
	private AnimLayer animLayer = null;

	private static ChallengeActivity mainActivity = null;

	public static ChallengeActivity getMainActivity() {
		return mainActivity;
	}

	public static final String SP_KEY_BEST_SCORE = "bestScore";

}
