package com.tuguzteam.netdungeons.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.badlogic.gdx.utils.GdxRuntimeException
import com.tuguzteam.netdungeons.Loader
import com.tuguzteam.netdungeons.net.NetworkManager
import com.tuguzteam.netdungeons.ui.ClickListener
import com.tuguzteam.netdungeons.ui.ContentHeader
import com.tuguzteam.netdungeons.ui.YesNoDialog
import com.tuguzteam.netdungeons.ui.Window
import kotlinx.coroutines.launch
import ktx.actors.plusAssign
import ktx.log.debug

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
            val buttonController = ButtonGroup(teamButton, slaughterButton)
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
            val buttonController = ButtonGroup(mediumButton, largeButton, vLargeButton)
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
            val buttonController = ButtonGroup(mansionButton, castleButton, slumButton)
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
//            addListener(ButtonListener {
//                content.cancel()
//                content.scrollTo(0f, scrollGroup.height / 2f,
//                    0f, scrollGroup.height / 2f)
//            })
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
                contentSplitPane.setSecondWidget(emptyContent)
                header.setFirstWidget(HorizontalGroup())
            })
        }
    }

    inner class NavRating {
        val navButton = ImageTextButton("Rating",
            ImageTextButtonStyle(null, null, null, BitmapFont())).apply {
            addListener(ClickListener {
                contentSplitPane.setSecondWidget(emptyContent)
                header.setFirstWidget(HorizontalGroup())
            })
        }
    }

    private val navigation = Table().apply {
        center().add(NavGame().navButton).expand().row()
        add(NavProfile().navButton).expand().row()
        add(NavRating().navButton).expand()
    }
    private val emptyContent = Table().apply {
        center().add(TextButton("Go to game screen", defaultSkin))
    }
    private val header = ContentHeader(this, HorizontalGroup(), defaultSkin)
    private val contentSplitPane = SplitPane(header, emptyContent,
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
            null
        }
        loader.addScreen(screen = RegistrationScreen(loader))
        loader.coroutineScope.launch {
            loader.networkManager.updateUser()
            if (loader.networkManager.user == null && registrationScreen == null) {
                loader.setScreen<RegistrationScreen>()
            } else NetworkManager.logger.debug(loader.networkManager.user::toString)
        }
    }

    override fun onBackPressed() {
        if (yesNoDialog.isHidden) yesNoDialog.show(this)
    }
}
