package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.badlogic.gdx.utils.GdxRuntimeException
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.net.NetworkManager
import com.tuguzteam.netdungeons.net.Result
import com.tuguzteam.netdungeons.ui.*
import com.tuguzteam.netdungeons.ui.Window
import kotlinx.coroutines.launch
import ktx.actors.plusAssign
import ktx.log.debug
import ktx.log.error
import ktx.log.info

class MainScreen(loader: Loader) : StageScreen(loader) {
    private val defaultSkin = loader.defaultSkin
    private val yesNoDialog =
        YesNoDialog("Are you sure you want to exit?", defaultSkin, Gdx.app::exit)

    inner class NavGame {
        inner class GameMode {
            private val teamButton = CheckBox("Team Fight", defaultSkin).apply {
                addListener(ClickListener {
                    if (isChecked) modeLabel.setText("Team Fight")
                })
            }
            private val slaughterButton = CheckBox("Slaughter", defaultSkin).apply {
                addListener(ClickListener {
                    if (isChecked) modeLabel.setText("Slaughter")
                })
            }
            val buttonController = RadioController(false, teamButton, slaughterButton)
            val window: Window = Window("Choose game mode", defaultSkin,
                teamButton, slaughterButton)
        }
        inner class GameSize {
            private val mediumButton = CheckBox("Medium", defaultSkin).apply {
                addListener(ClickListener {
                    if (isChecked) sizeLabel.setText("Medium")
                })
            }
            private val largeButton = CheckBox("Large", defaultSkin).apply {
                addListener(ClickListener {
                    if (isChecked) sizeLabel.setText("Large")
                })
            }
            private val vLargeButton = CheckBox("Very Large", defaultSkin).apply {
                addListener(ClickListener {
                    if (isChecked) sizeLabel.setText("Very Large")
                })
            }
            val buttonController = RadioController(false,
                mediumButton, largeButton, vLargeButton)
            val window: Window = Window("Choose map size", defaultSkin,
                mediumButton, largeButton, vLargeButton)
        }
        inner class GameType {
            private val mansionButton = CheckBox("Mansion", defaultSkin).apply {
                addListener(ClickListener {
                    if (isChecked) typeLabel.setText("Mansion")
                })
            }
            private val castleButton = CheckBox("Castle", defaultSkin).apply {
                addListener(ClickListener {
                    if (isChecked) typeLabel.setText("Castle")
                })
            }
            private val slumButton = CheckBox("Slum", defaultSkin).apply {
                addListener(ClickListener {
                    if (isChecked) typeLabel.setText("Slum")
                })
            }
            val buttonController = RadioController(false,
                mansionButton, castleButton, slumButton)
            val window: Window = Window("Choose amounts of treasure", defaultSkin,
                mansionButton, castleButton, slumButton)
        }

        private val scrollGroup = VerticalGroup().apply {
            pad(Gdx.graphics.height / 6f)
            space(Gdx.graphics.height / 4f)
            Loader.logger.debug { "${this.height}" }
            this += GameMode().window
            this += GameSize().window
            this += GameType().window
        }
        private val content = ScrollPane(scrollGroup).apply {
            setOverscroll(false, false)
        }
        private val modeLabel = Label("Mode", defaultSkin).apply {
            addListener(ClickListener {
                content.cancel()
                content.scrollTo(0f, scrollGroup.height, 0f, scrollGroup.height)
            })
        }
        private val inLabel = Label("in", defaultSkin)
        private val sizeLabel = Label("Size", defaultSkin).apply {
            addListener(ClickListener {
                content.cancel()
                content.scrollTo(0f, scrollGroup.height / 2f,
                    0f, scrollGroup.height / 2f)
            })
        }
        private val typeLabel = Label("Type", defaultSkin).apply {
            addListener(ClickListener {
                content.cancel()
                content.scrollTo(0f, 0f, 0f, 0f)
            })
        }
        private val headerContent = HorizontalGroup().apply {
            center().space(Gdx.graphics.height / 20f)
            this += modeLabel
            this += inLabel
            this += sizeLabel
            this += typeLabel
        }
        private val playButton = ImageTextButton("Play",
            ImageTextButtonStyle(null, null, null, BitmapFont())).apply {
            addListener(ClickListener {
                loader.setScreen<GameScreen>()
            })
        }
        private val headerSplitPane = SplitPane(headerContent, Container(playButton),
            false, defaultSkin).apply {
            maxSplitAmount = 0.85f
            minSplitAmount = 0.85f
        }
        val navButton = ImageTextButton("Game",
            ImageTextButtonStyle(null, null, null, BitmapFont())).apply {
            addListener(ClickListener {
                contentSplitPane.setSecondWidget(content)
                header.setFirstWidget(headerSplitPane)
            })
        }
    }

    inner class NavProfile {
        val navButton = ImageTextButton("Profile",
            ImageTextButtonStyle(null, null, null, BitmapFont())).apply {
            addListener(ClickListener {
                contentSplitPane.setSecondWidget(null)
                header.setFirstWidget(HorizontalGroup())
            })
        }
    }

    inner class NavRating {
        private val levelButton = CheckBox("By level", defaultSkin)
        private val winButton = CheckBox("By wins", defaultSkin)
        private val killButton = CheckBox("By kills", defaultSkin)
        private val buttonController = RadioController(true,
            levelButton, winButton, killButton)
        private val sortButtons = HorizontalGroup().apply {
            left().space(Gdx.graphics.height / 20f).padLeft(Gdx.graphics.height / 20f)
            this += levelButton
            this += winButton
            this += killButton
        }
        val navButton = ImageTextButton("Rating",
            ImageTextButtonStyle(null, null, null, BitmapFont())).apply {
            addListener(ClickListener {
                contentSplitPane.setSecondWidget(null)
                header.setFirstWidget(sortButtons)
            })
        }
    }

    private val navigation = Table().apply {
        center().add(NavGame().navButton).expand().row()
        add(NavProfile().navButton).expand().row()
        add(NavRating().navButton).expand()
    }
    private val header = ContentHeader(this, null, defaultSkin)
    private val contentSplitPane = SplitPane(header, null,
        true, defaultSkin).apply {
        maxSplitAmount = 0.15f
        minSplitAmount = 0.15f
    }
    private val mainSplitPane = SplitPane(navigation, contentSplitPane,
        false, defaultSkin).apply {
        setFillParent(true)
        maxSplitAmount = 0.15f
        minSplitAmount = 0.15f
    }

    init {
        isDebugAll = true
        this += mainSplitPane
        loader.addScreen(screen = GameScreen(loader, this))
    }

    override fun show() {
        super.show()
        Loader.logger.debug { "Main menu screen is shown..." }
        val registrationScreen = try {
            loader.getScreen<RegistrationScreen>()
        } catch (e: GdxRuntimeException) {
            loader.addScreen(screen = RegistrationScreen(loader))
            null
        }
        loader.coroutineScope.launch {
            when (val result = loader.networkManager.updateUser()) {
                is Result.Cancel -> Loader.logger.info { "Task was cancelled normally" }
                is Result.Failure -> Loader.logger.error(result.cause) { "User update failure!" }
                is Result.Success -> {
                    if (registrationScreen == null) loader.setScreen<RegistrationScreen>()
                    else NetworkManager.logger.debug(result.data::toString)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (yesNoDialog.isHidden) yesNoDialog.show(this)
    }
}
