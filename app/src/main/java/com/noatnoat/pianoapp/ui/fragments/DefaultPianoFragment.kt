package com.noatnoat.pianoapp.ui.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.Manifest
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.noatnoat.pianoapp.R
import com.noatnoat.pianoapp.databinding.FragmentDefaultPianoBinding
import com.noatnoat.pianoapp.helpers.AudioRecorderHelper
import com.noatnoat.pianoapp.helpers.PermissionHelper
import com.noatnoat.pianoapp.helpers.piano.AutoPlayPianoHelper
import com.noatnoat.pianoapp.helpers.piano.KeyboardEventHelper
import com.noatnoat.pianoapp.helpers.piano.PianoKeyPlayerHelper
import com.noatnoat.pianoapp.helpers.piano.PianoSettingHelper
import com.noatnoat.pianoapp.helpers.piano.SeekBarHelper
import com.noatnoat.pianoapp.models.PianoKeyMapping
import com.noatnoat.pianoapp.models.PianoKeys
import com.noatnoat.pianoapp.models.SongRepository
import com.noatnoat.pianoapp.ui.activities.MainActivity
import com.noatnoat.pianoapp.ui.base.BaseFragment
import com.noatnoat.pianoapp.ui.callbacks.OnSongSelectedListener
import com.noatnoat.pianoapp.ui.callbacks.PianoScrollListener
import com.noatnoat.pianoapp.ui.dialogs.Mp3ListDialogFragment
import com.noatnoat.pianoapp.ui.dialogs.RecordDialogFragment
import com.noatnoat.pianoapp.ui.dialogs.SettingDialogFragment
import com.noatnoat.pianoapp.ui.dialogs.SongListDialogFragment
import java.util.LinkedList
import java.util.Queue

class DefaultPianoFragment : BaseFragment(), PianoScrollListener, OnSongSelectedListener {
    private lateinit var soundPool: SoundPool
    private lateinit var soundIds: SparseIntArray
    private lateinit var pianoKeyPlayerHelper: PianoKeyPlayerHelper
    private lateinit var audioRecorderHelper: AudioRecorderHelper
    private lateinit var loadingProgressBar: ProgressBar
    private var isRecording = false
    private lateinit var seekBarHelper: SeekBarHelper
    private lateinit var keys: Array<Button>
    private val pianoKeys = PianoKeys.getInstance()
    private lateinit var keyboardEventHelper: KeyboardEventHelper
    private val whiteKeyIds = pianoKeys.whiteKeys.map { it.keyId }.toTypedArray()
    private val blackKeyIds = pianoKeys.blackKeys.map { it.keyId }.toTypedArray()
    private val whiteKeySounds = pianoKeys.whiteKeys.map { it.soundId }.toTypedArray()
    private val blackKeySounds = pianoKeys.blackKeys.map { it.soundId }.toTypedArray()
    private var _binding: FragmentDefaultPianoBinding? = null
    private val binding get() = _binding!!
    private var isInLearnMode = false
    private var queue: Queue<Int> = LinkedList()
    private var isPlaying = false
    private lateinit var autoPlayPianoHelper: AutoPlayPianoHelper
    private var areNotesVisible = true
    private lateinit var permissionHelper: PermissionHelper

    @OptIn(UnstableApi::class)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefaultPianoBinding.inflate(inflater, container, false)
        val view = binding.root
        initializeKeys()
        keyboardEventHelper = KeyboardEventHelper(keys)
        setFullScreenMode()
        initializeSoundPool()
        setupPianoKeyPlayerHelper()
        loadSounds()
        initializeSeekBar()
        setupButtons()
        setupRecording()
        setupNavigationButtons()
        autoPlayPianoHelper = AutoPlayPianoHelper(requireContext(), PianoKeys.getInstance(), binding.root, this)
        setupPlayButton()
        setupStyleButton()
        setupPlaylistButton()
        setupLearnPiano()
        setupSetting()
        setFragmentLandscapeOrientation()
//        setupKeyWidthAdjustmentButtons()

        binding.layoutPianoOptions.songList.text = SongRepository().songs.first().name
        binding.layoutPianoOptions.importBtn.setOnClickListener {
            val permissionsToRequest = mutableListOf<String>()
            val hasStoragePermission = checkStoragePermissions()

            if (!hasStoragePermission) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }

            if (permissionsToRequest.isNotEmpty()) {
                requestPermissions(permissionsToRequest.toTypedArray(), PermissionHelper.PERMISSION_REQUEST_ACCESS_MUSIC)
            } else {
                showMp3ListDialog()
            }
        }
        permissionHelper = PermissionHelper(this, requireContext())

        return view
    }


    private fun setFullScreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    private fun initializeSoundPool() {
        soundPool = SoundPool.Builder().setMaxStreams(10).build()
        soundIds = SparseIntArray()
    }

    private fun setupPianoKeyPlayerHelper() {
        pianoKeyPlayerHelper = PianoKeyPlayerHelper(requireContext())
        pianoKeyPlayerHelper.loadSounds(whiteKeySounds, blackKeySounds, whiteKeyIds, blackKeyIds)
    }

    private var allSoundsLoaded = false

    private fun loadSounds() {
        whiteKeySounds.forEachIndexed { index, soundRes ->
            soundIds.put(whiteKeyIds[index], soundPool.load(context, soundRes, 1))
        }
        blackKeySounds.forEachIndexed { index, soundRes ->
            soundIds.put(blackKeyIds[index], soundPool.load(context, soundRes, 1))
        }
        soundPool.setOnLoadCompleteListener { _, _, _ ->
            if (allSoundsLoaded && soundIds.size() == (whiteKeySounds.size + blackKeySounds.size)) {
                allSoundsLoaded = true
                loadingProgressBar.visibility = View.INVISIBLE
                keys.forEach { it.isEnabled = true }
                Log.d("SoundLoading", "All sounds loaded successfully")
            }
        }
    }

    private fun initializeSeekBar() {
        binding.layoutPianoKeyboard.scrollView.post {
            binding.layoutPianoKeyboard.scrollView.scrollTo((binding.layoutPianoKeyboard.scrollView.getChildAt(0).width * 0.55).toInt(), 0)
        }

        seekBarHelper = SeekBarHelper(binding.layoutPianoSeekbar.seekBar, binding.layoutPianoKeyboard.scrollView)
        seekBarHelper.setupSeekBar()
//        binding.layoutPianoSeekbar.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                updateArrowButtonTints()
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//            }
//        })
    }


    private fun updateArrowButtonTints() {
        val currentProgress = binding.layoutPianoSeekbar.seekBar.progress
        val maxProgress = binding.layoutPianoSeekbar.seekBar.max
        val leftButton = binding.layoutPianoSeekbar.buttonArrowLeft
        val rightButton = binding.layoutPianoSeekbar.buttonArrowRight

        if (currentProgress <= 0) {
            leftButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            leftButton.clearColorFilter()
        }

        if (currentProgress >= maxProgress) {
            rightButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            rightButton.clearColorFilter()
        }
    }

    private fun setupButtons() {
        binding.layoutPianoSeekbar.buttonArrowLeft.setOnClickListener {
            val currentProgress = binding.layoutPianoSeekbar.seekBar.progress
            if (currentProgress > 0) {
                binding.layoutPianoSeekbar.seekBar.progress = currentProgress - 10
            }
        }

        binding.layoutPianoSeekbar.buttonArrowRight.setOnClickListener {
            val currentProgress = binding.layoutPianoSeekbar.seekBar.progress
            if (currentProgress < binding.layoutPianoSeekbar.seekBar.max) {
                binding.layoutPianoSeekbar.seekBar.progress = currentProgress + 10
            }
        }

        binding.layoutPianoSeekbar.buttonZoomOut.setOnClickListener {
            val layoutPianoOptions = binding.layoutPianoOptions.root
            if (layoutPianoOptions.visibility == View.VISIBLE) {
                layoutPianoOptions.visibility = View.GONE
                (it as ImageButton).setImageResource(R.drawable.ic_zoom_in)
            } else {
                layoutPianoOptions.visibility = View.VISIBLE
                (it as ImageButton).setImageResource(R.drawable.ic_zoom_out)
            }
        }

        binding.layoutPianoOptions.hideNoteBtn.setOnClickListener {
            toggleNoteVisibility()
        }
    }

    private fun initializeKeys() {
        val whiteKeys = whiteKeyIds.map { keyId ->
            binding.root.findViewById<Button>(keyId).apply {
                setOnTouchListener(createKeyTouchListener(keyId))
            }
        }

        val blackKeys = blackKeyIds.map { keyId ->
            binding.root.findViewById<Button>(keyId).apply {
                setOnTouchListener(createKeyTouchListener(keyId))
            }
        }

        keys = (whiteKeys + blackKeys).toTypedArray()

    }


    private fun getKeyIdsForSong(songName: String): Queue<Int> {
        val song = SongRepository().songs.find { it.name == songName }

        val keyIdsQueue = LinkedList<Int>()

        song?.let {
            for (note in it.notes) {
                PianoKeyMapping.noteToKeyIdMap[note]?.let { keyId ->
                    keyIdsQueue.offer(keyId)
                }
            }
        }

        return keyIdsQueue
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createKeyTouchListener(keyId: Int) = View.OnTouchListener { view, event ->
        if (isInLearnMode) {
            queue.peek()?.let { setKeyPressedState(it) }
            if (!queue.isEmpty() && queue.peek() == keyId) {
                setKeyUnPressedState(keyId)
                scrollToKey(keyId)
                queue.poll()
                if (queue.isEmpty()) {
                    isInLearnMode = false
                    activity?.runOnUiThread {
                        binding.layoutPianoOptions.stepPianoBtn.setBackgroundColor(resources.getColor(R.color.white))
                    }
                }
            }
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.isPressed = true
                pianoKeyPlayerHelper.playSound(keyId)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                view.isPressed = false
            }
        }
        true
    }

    private fun setKeyPressedState(keyId: Int) {
        val keyButton = requireActivity().findViewById<Button>(keyId)
        if (whiteKeyIds.contains(keyId)) {
            keyButton.setBackgroundResource(R.drawable.img_white_auto)
        } else if (blackKeyIds.contains(keyId)) {
            keyButton.setBackgroundResource(R.drawable.img_black_auto)
        }
    }

    private fun setKeyUnPressedState(keyId: Int) {
        val keyButton = requireActivity().findViewById<Button>(keyId)
        if (whiteKeyIds.contains(keyId)) {
            keyButton.setBackgroundResource(R.drawable.white_piano_key)
        } else if (blackKeyIds.contains(keyId)) {
            keyButton.setBackgroundResource(R.drawable.black_piano_key)
        }
    }



    private fun setupStyleButton() {
        binding.layoutPianoOptions.styleBtn.setOnClickListener {
            val action = DefaultPianoFragmentDirections.actionPianoFragmentToPianoStylesFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecording() {
        val recordButtonLayout = binding.layoutPianoOptions.recordBtn
        val recordImageButton = binding.layoutPianoOptions.imgRecord
        val txtRecord = binding.layoutPianoOptions.txtRecord
        audioRecorderHelper = AudioRecorderHelper(requireActivity(), txtRecord)

        recordButtonLayout.setOnClickListener {
            val permissionsToRequest = mutableListOf<String>()
            val hasRecordPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            if (!hasRecordPermission) {
                permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
            }

            val hasStoragePermission = checkStoragePermissions()
            if (!hasStoragePermission) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }

            if (permissionsToRequest.isNotEmpty()) {
                requestPermissions(permissionsToRequest.toTypedArray(), PermissionHelper.PERMISSION_REQUEST_RECORD_AUDIO)
            } else {
                toggleRecordingState(recordImageButton, txtRecord)
            }
        }
    }

    private fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val hasRecordPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val hasStoragePermission = checkStoragePermissions()

        if (hasRecordPermission && hasStoragePermission) {
            toggleRecordingState(binding.layoutPianoOptions.imgRecord, binding.layoutPianoOptions.txtRecord)
        } else {
//            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleRecordingState(recordImageButton: ImageView, txtRecord: TextView) {
        if (isRecording) {
            audioRecorderHelper.stopRecording()
            isRecording = false
            recordImageButton.setImageResource(R.drawable.ic_record_play)
            showRecordDialog()
            txtRecord.text = "REC"
        } else {
            audioRecorderHelper.startRecording()
            isRecording = true
            recordImageButton.setImageResource(R.drawable.ic_record_stop)
            txtRecord.text = "STOP"
        }
    }

    private fun showMp3ListDialog() {
        val dialogFragment = Mp3ListDialogFragment()
        dialogFragment.show(parentFragmentManager, "Mp3ListDialogFragment")
    }

    private fun setupLearnPiano() {
        binding.layoutPianoOptions.stepPianoBtn.setOnClickListener {
            if (isInLearnMode) {
                isInLearnMode = false
                queue.clear()
                binding.layoutPianoOptions.stepPianoBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hand_point_up, 0, 0, 0)
            } else {
                isInLearnMode = true
                Log.d("Queue123456", queue.toString())
                queue.peek()?.let { setKeyPressedState(it) }
                binding.layoutPianoOptions.stepPianoBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_record_stop, 0, 0, 0)
            }
        }
    }

    private fun setupNavigationButtons() {
        binding.layoutPianoOptions.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.layoutPianoOptions.twoPeoplesBtn.setOnClickListener {
            navigateTo(R.id.action_pianoFragment_to_twoPlayerPianoFragment)
        }

        binding.layoutPianoOptions.twoKeyboardBtn.setOnClickListener {
            navigateTo(R.id.action_pianoFragment_to_twoKeyboardPianoFragment)
        }
    }

    private fun navigateTo(actionId: Int) {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_left)
            .setPopEnterAnim(R.anim.slide_in_right)
            .build()
        findNavController().navigate(actionId, null, navOptions)
    }

    private fun setupPlayButton() {
        binding.layoutPianoOptions.playBtn.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                binding.layoutPianoOptions.playBtn.setImageResource(R.drawable.ic_pause)
                val songRepository = SongRepository()
                val songPlay =
                    songRepository.songs.find { it.name == binding.layoutPianoOptions.songList.text.toString() }
                songPlay?.let { song ->
                    val keyIds = song.notes.mapNotNull { noteName ->
                        PianoKeyMapping.noteToKeyIdMap[noteName]
                    }
                    autoPlayPianoHelper.autoPlay(keyIds)
                }
                keyboardEventHelper.disableKeyboard()
            } else {
                autoPlayPianoHelper.stopAutoPlay()
                binding.layoutPianoOptions.playBtn.setImageResource(R.drawable.ic_play)
                keyboardEventHelper.enableKeyboard()
            }
        }
    }

    private fun setupSetting() {
        binding.layoutPianoOptions.optionsBtn.setOnClickListener {

            val settingDialogFragment = SettingDialogFragment()
            settingDialogFragment.show(parentFragmentManager, "SettingDialogFragment")
        }
    }
    private fun setupPlaylistButton() {
        binding.layoutPianoOptions.songList.setOnClickListener {
            if (!isPlaying && !isInLearnMode) {
                val songNames = SongRepository().songs.map { it.name }.toTypedArray()
                showSongListDialog(songNames)

            } else {
                Toast.makeText(context, "Cannot change songs while playing or in learn mode.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSongListDialog(songs: Array<String>) {
        val dialog = SongListDialogFragment(songs, this)
        dialog.show(parentFragmentManager, "SongListDialogFragment")
    }

    private fun showRecordDialog() {
        val dialog = RecordDialogFragment(audioRecorderHelper)
        dialog.show(parentFragmentManager, "RecordDialogFragment")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        pianoKeyPlayerHelper.release()
        audioRecorderHelper.stopRecording()
        resetFullScreenMode()
    }

    private fun resetFullScreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    // Iterate over all keys and adjust their width
//
//    private val MIN_KEY_WIDTH = 50 // Minimum width in pixels
//    private val MAX_KEY_WIDTH = 150
//    private fun adjustKeyWidth(delta: Int) {
//        keys.forEach { key ->
//            val currentWidth = key.layoutParams.width
//            val newWidth = (currentWidth + delta).coerceIn(MIN_KEY_WIDTH, MAX_KEY_WIDTH)
//            key.layoutParams.width = newWidth
//            key.requestLayout() // This is necessary to apply the new width
//        }
//    }
//
//    private fun setupKeyWidthAdjustmentButtons() {
//        binding.layoutPianoSeekbar.buttonMinus.setOnClickListener {
//            adjustKeyWidth(-10) // Adjust by -10 pixels or any other value you see fit
//        }
//
//        binding.layoutPianoSeekbar.buttonAdd.setOnClickListener {
//            adjustKeyWidth(10) // Adjust by 10 pixels or any other value you see fit
//        }
//    }

    override fun scrollToKey(keyId: Int) {
        val keyView = view?.findViewById<Button>(keyId)
        keyView?.let { key ->
            val scrollView = binding.layoutPianoKeyboard.scrollView
            val seekBar = binding.layoutPianoSeekbar.seekBar

            scrollView.post {
                val scrollX = key.left

                val maxScrollX = scrollView.getChildAt(0).width - scrollView.width
                val validScrollX = scrollX.coerceIn(0, maxScrollX)

                scrollView.scrollTo(validScrollX, 0)

                val totalWidth = scrollView.getChildAt(0).width
                val scrollFraction = validScrollX.toFloat() / totalWidth

                val seekBarProgress = (scrollFraction * seekBar.max).toInt()
                seekBar.progress = seekBarProgress
            }
        }
    }
    override fun onSongSelected(songName: String) {
        binding.layoutPianoOptions.songList.text = songName

        queue = getKeyIdsForSong(songName)
        if (isInLearnMode) {
            binding.layoutPianoOptions.stepPianoBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hand_point_up, 0, 0, 0)
            isInLearnMode = false
        }
    }

    private fun toggleNoteVisibility() {
        areNotesVisible = !areNotesVisible
        keys.forEach { key ->
        }
        binding.layoutPianoOptions.hideNoteBtn.setImageResource(
            if (areNotesVisible) R.drawable.ic_hide_note else R.drawable.ic_show_note
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentPortraitOrientation()
    }


}
